package py.com.aruba.clientes.ui.appointment.fragments.recyclerProfessionals;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import java.util.List;

import io.realm.Realm;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.models.Appointment;
import py.com.aruba.clientes.data.models.Professional;
import py.com.aruba.clientes.data.models.ProfessionalAvailableHours;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.ui.appointment.AppointmentReservationActivity;
import py.com.aruba.clientes.ui.appointment.dialogs.SelectedProfessionalDialog;
import py.com.aruba.clientes.ui.appointment.fragments.Fragment02;
import py.com.aruba.clientes.ui.appointment.fragments.recyclerProfessionals.hours.HoursViewAdapter;
import py.com.aruba.clientes.utils.Constants;
import py.com.aruba.clientes.utils.Print;
import py.com.aruba.clientes.utils.images.UtilsImage;

public class ProfessionalViewAdapter extends RecyclerView.Adapter<ProfessionalViewHolder> {
    private static final String TAG = "ReviewsViewAdapter";

    // Variables
    private final Activity activity;
    private List<Professional> lista;
    private Realm realm;
    private Appointment appointment;
    private Fragment02.OnHourItemClicked listener;
    private int previosClicked = -1;
    private int service_duration;

    public ProfessionalViewAdapter(Activity activity, List<Professional> lista, int schedule_id, int service_duration, Fragment02.OnHourItemClicked listener) {
        this.activity = activity;
        this.lista = lista;
        this.realm = GlobalRealm.getDefault();
        this.listener = listener;
        this.service_duration = service_duration;
        appointment = realm.where(Appointment.class).equalTo("id", schedule_id).findFirst();
    }

    @Override
    public ProfessionalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(activity).inflate(R.layout.item_professional, null);
        return new ProfessionalViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(final ProfessionalViewHolder holder, int position) {
        final Professional object = lista.get(holder.getAdapterPosition());
        holder.tvName.setText(object.getFullName());
        holder.tvRatingCount.setText(getReviewCount(object.getReviews_count()));
        holder.ratingBar.setRating(Float.parseFloat(object.getAverage_reviews()));
        UtilsImage.loadImageCircle(activity, holder.ivAvatar, object.getAvatar(), Constants.PLACEHOLDERS.AVATAR);

//        holder.ivAvatar.setOnClickListener(v -> {
//            setItemClick(object);
//        });

        // Obtenemos el horario de los profesionales
        List<JsonObject> hours = ProfessionalAvailableHours.get_hours(realm, object.getId(), service_duration);

        if (hours.size() < 1) {
            holder.tvNoAvailability.setVisibility(View.VISIBLE);
        } else {
            holder.tvNoAvailability.setVisibility(View.GONE);
            initHoursRecycler(hours, holder.recyclerViewHours, holder.getAdapterPosition(), object);
        }
    }

    private String getReviewCount(int reviews_count) {
        String cant = reviews_count == 1 ? "calificación" : "calificaciones";
        return "( " + reviews_count + " " + cant + ")";
    }


    /**
     * Método para inicializar el recycler del calendario
     */
    private void initHoursRecycler(List<JsonObject> list, RecyclerView recyclerViewHours, int pos, Professional professional) {

        // Acá inicializamos el recycler del calendario
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewHours.setVisibility(View.VISIBLE);
        recyclerViewHours.setHasFixedSize(true);
        recyclerViewHours.setLayoutManager(mLayoutManager);
        HoursViewAdapter hoursViewAdapter = new HoursViewAdapter(activity, list, item -> {
            // Si se clickea el mismo, retornamos
            if (previosClicked == pos) return;

            // Enviamos al listener el item que se clickeo
            item.addProperty("professional_id", professional.getId());
            item.addProperty("professional", professional.getFullName());
            listener.onHourItemClicked(item);
            this.notifyItemChanged(previosClicked);

            previosClicked = pos;
        });
        recyclerViewHours.setAdapter(hoursViewAdapter);
    }

    /**
     * Seteamos el evento click del card
     *
     * @param object
     */
    private void setItemClick(Professional object) {
        SelectedProfessionalDialog customDialog = new SelectedProfessionalDialog();
        customDialog.setData(object.getId(), appointment.getId(), activity);
        customDialog.show(((AppointmentReservationActivity) activity).getSupportFragmentManager(), "dialog");
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void update(List<Professional> listReviews) {
        this.lista = listReviews;
        this.notifyDataSetChanged();
    }
}
