package py.com.aruba.clientes.ui.appointment.fragments.recyclerAppointments;

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
import py.com.aruba.clientes.ui.appointment.AppointmentReservationActivity;
import py.com.aruba.clientes.utils.TimeUtils;

public class AppointmentViewAdapter extends RecyclerView.Adapter<AppointmentViewHolder> {
    private static final String TAG = "AppointmentViewAdapter";

    // Variables
    private final AppointmentReservationActivity activity;
    private List<Appointment> lista;
    private Realm realm;
    private List<AppointmentViewHolder> switchList;

    public AppointmentViewAdapter(AppointmentReservationActivity activity, List<Appointment> lista) {
        this.activity = activity;
        this.lista = lista;
        this.realm = GlobalRealm.getDefault();
        switchList = new ArrayList<>();
    }

    @Override
    public AppointmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(activity).inflate(R.layout.item_schedule_clients, null);
        return new AppointmentViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(final AppointmentViewHolder holder, int position) {
        Appointment object = lista.get(holder.getAdapterPosition());
        int pos = holder.getAdapterPosition();

        // Seteamos los datos
        setData(object, holder);


        // TODO: Ver para utilizar la animación del explode
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
//            RealmResults<AppointmentDetails> sd = realm.where(AppointmentDetails.class).equalTo("appointment.id", schedule_id).findAll();
//            Appointment schRealm = realm.where(Appointment.class).equalTo("id", schedule_id).findFirst();
//            if (sd.size() > 0) sd.deleteAllFromRealm();
//            if (schRealm != null) schRealm.deleteFromRealm();
//
//            realm.commitTransaction();
//
//            new Handler().postDelayed(() -> {
//
//
//            }, 350);
//        });


        // TODO:Falta esta sección
        holder.ivEditService.setVisibility(View.GONE);
        holder.ivEditProfessional.setVisibility(View.GONE);
        // Editar
//        holder.ivEditService.setOnClickListener(v -> {
//            Appointment schRealm = realm.where(Appointment.class).equalTo("id", lista.get(pos).getId()).findFirst();
//            realm.beginTransaction();
//            schRealm.setStatus(Constants.APPOINTMENT_STATUS.EDITING);
//            realm.commitTransaction();
//
//            activity.editAppointment(0, schRealm.getId());
//        });
//
//        holder.ivEditProfessional.setOnClickListener(v -> {
//            Appointment schRealm = realm.where(Appointment.class).equalTo("id", lista.get(pos).getId()).findFirst();
//            realm.beginTransaction();
//            schRealm.setStatus(Constants.APPOINTMENT_STATUS.EDITING);
//            realm.commitTransaction();
//
//            activity.editAppointment(1, schRealm.getId());
//        });
    }

    /**
     * Método para setear los datos de esta pantalla
     */
    public void setData(Appointment appointment, AppointmentViewHolder holder) {
        UserAddress address = realm.where(UserAddress.class).equalTo("backend_id", appointment.getAddress_id()).findFirst();

        holder.tvTitle.setText(String.format("Resumen de servicios para %s", appointment.getClient()));
        holder.tvCategory.setText(appointment.getCategory());
        holder.tvAddress.setText(appointment.getAddress());
        holder.tvTypeAddress.setText(address.getName().toUpperCase());
        holder.tvProfessional.setText(appointment.getProfessional());
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
