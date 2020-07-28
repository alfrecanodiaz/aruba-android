package py.com.aruba.clientes.ui.cart.recyclerSchedules;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.models.Appointment;
import py.com.aruba.clientes.data.models.AppointmentDetails;
import py.com.aruba.clientes.data.models.UserAddress;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.utils.TimeUtils;

public class AppointmentViewAdapter extends RecyclerView.Adapter<ScheduleViewHolder> {
    private static final String TAG = "ReviewsViewAdapter";

    // Variables
    private final Context context;
    private List<Appointment> lista;
    private Realm realm;
    private List<ScheduleViewHolder> switchList;

    public AppointmentViewAdapter(Context context, List<Appointment> lista) {
        this.context = context;
        this.lista = lista;
        this.realm = GlobalRealm.getDefault();
        switchList = new ArrayList<>();
    }

    @Override
    public ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(context).inflate(R.layout.item_schedule_clients_cart, null);
        return new ScheduleViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(final ScheduleViewHolder holder, int position) {
        Appointment object = lista.get(holder.getAdapterPosition());
        int pos = holder.getAdapterPosition();

        // Seteamos los datos
        setData(object, holder);

        // Majenamos el view del expandable
//        holder.rlContentTitle.setOnClickListener(v -> holder.expandableLayout.toggle());

        // Eliminar una reserva
//        holder.ivDelete.setOnClickListener(v -> {
////            Utils.removeExplode((AppCompatActivity) context, holder.item);
//
//
//            // Eliminamos del listado de memoria
//            notifyItemRemoved(pos);
//            Appointment sch = lista.get(pos);
//            lista.remove(pos);
//
//            // Eliminamos desde la BD
//            realm.beginTransaction();
//            int schedule_id = sch.getId(realm);
//
//            AppointmentDetails sd = realm.where(AppointmentDetails.class).equalTo("appointment.id", schedule_id).findFirst();
//            Appointment schRealm = realm.where(Appointment.class).equalTo("id", schedule_id).findFirst();
//            if (sd != null) sd.deleteFromRealm();
//            if (schRealm != null) schRealm.deleteFromRealm();
//
//            realm.commitTransaction();
//
//            new Handler().postDelayed(() -> {
//
//
//            }, 350);
//        });

    }

    /**
     * Método para setear los datos de esta pantalla
     */
    public void setData(Appointment appointment, ScheduleViewHolder holder) {
        UserAddress address = realm.where(UserAddress.class).equalTo("backend_id", appointment.getAddress_id()).findFirst();

//        holder.tvTitle.setText(String.format("Servicios para %s", appointment.getClient()));
        holder.tvCategory.setText(appointment.getCategory());
        holder.tvAddress.setText(appointment.getAddress());
        holder.tvProfessional.setText(appointment.getProfessional());
        holder.tvTypeAddress.setText(address.getName().toUpperCase());
        holder.tvDate.setText(String.format("%s a las %s", TimeUtils.getDateStringReadable(appointment.getDate()), appointment.getHour_start_pretty()));

        RealmResults<AppointmentDetails> listSubService = realm.where(AppointmentDetails.class).equalTo("appointment.id", appointment.getId()).findAll();
        StringBuilder details = new StringBuilder();
        for (AppointmentDetails sd : listSubService) {
            details.append("-").append(sd.getTitle()).append(" ");
        }

        holder.tvSubService.setText(details);
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

    public void update(List<Appointment> listReviews) {
        this.lista = listReviews;
        this.notifyDataSetChanged();
    }
}
