package py.com.aruba.clientes.ui.appointment.fragments.recyclerSubServices;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.eventbus.BusEvents;
import py.com.aruba.clientes.data.eventbus.GlobalBus;
import py.com.aruba.clientes.data.models.Appointment;
import py.com.aruba.clientes.data.models.AppointmentDetails;
import py.com.aruba.clientes.data.models.SubService;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.ui.appointment.AppointmentReservationActivity;
import py.com.aruba.clientes.ui.appointment.dialogs.SubserviceDetailDialog;
import py.com.aruba.clientes.ui.appointment.fragments.Fragment01;
import py.com.aruba.clientes.utils.Utils;
import py.com.aruba.clientes.utils.dialogs.CustomDialog;

public class SubServicesViewAdapter extends RecyclerView.Adapter<SubServicesViewHolder> {
    private static final String TAG = "ReviewsViewAdapter";

    // Variables
    private final Context context;
    private final Fragment01 fragment01;
    private List<SubService> lista;
    private Realm realm;
    private Appointment appointment;
    private List<SubServicesViewHolder> switchList;

    public SubServicesViewAdapter(Context context, List<SubService> lista, Appointment appointment, Fragment01 fragment01) {
        this.context = context;
        this.lista = lista;
        this.appointment = appointment;
        this.fragment01 = fragment01;
        this.realm = GlobalRealm.getDefault();
        switchList = new ArrayList<>();
    }

    @Override
    public SubServicesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(context).inflate(R.layout.item_subservices_switch, null);
        return new SubServicesViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(final SubServicesViewHolder holder, int position) {
        switchList.add(holder);

        final SubService object = lista.get(holder.getAdapterPosition());
        holder.tvTitle.setText(object.getDisplay_name());
        holder.tvPrice.setText("Gs. " + Utils.miles(object.getPrice()));
        holder.tvDetails.setOnClickListener(v -> setItemClick(object, holder.idSwService));

        holder.idSwService.setOnCheckedChangeListener(null);
        holder.idSwService.setChecked(isChecked(object));
        holder.idSwService.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Obtenemos el objeto desde la BD, porque si usamos el que está en memoria no siempre estará actualizado
//            SubService updatedObject = realm.where(SubService.class).equalTo("id", lista.get(holder.getAdapterPosition()).getId()).findFirst();

            if (isChecked) {
                realm.beginTransaction();
                // Guardamos los datos del subservicio
                AppointmentDetails appointmentDetails = new AppointmentDetails();
                appointmentDetails.setAppointment(appointment);
                appointmentDetails.setSubService(object);

                appointmentDetails.setId(appointmentDetails.getId(realm));
                appointmentDetails.setTitle(object.getDisplay_name());

                appointmentDetails.setSub_service_id(object.getId());
                appointmentDetails.setSub_category_id(object.getSubCategory().getId());

                appointmentDetails.setPrice(object.getPrice());
                appointmentDetails.setTime(object.getTime());
                realm.copyToRealmOrUpdate(appointmentDetails);

                // Actualizamos el precio del appointment
                appointment.setPrice(appointment.getPrice() + object.getPrice());
                realm.copyToRealmOrUpdate(appointment);

                realm.commitTransaction();
            } else {
                AppointmentDetails o = AppointmentDetails.getSpecificDetail(realm, appointment, object);
                if (o != null) {
                    realm.beginTransaction();
                    o.deleteFromRealm();

                    // Actualizamos el precio del appointment
                    appointment.setPrice(appointment.getPrice() - object.getPrice());
                    realm.copyToRealmOrUpdate(appointment);

                    realm.commitTransaction();
                }
            }
            canSwipped();
        });
    }

    /**
     * Verificamos si el sub-servicio está en el detalle del appointment para checkear o descheckear
     *
     * @param object
     * @return
     */
    private boolean isChecked(SubService object) {
        AppointmentDetails sd = AppointmentDetails.getSpecificDetail(realm, appointment, object);
        return (sd != null);
    }

    /**
     * Método para sumar todos los sub-servicios
     */
    private void canSwipped() {
        RealmResults<AppointmentDetails> listSubService = realm.where(AppointmentDetails.class).findAll();
        fragment01.canSwipe = (listSubService.size() > 0);
        GlobalBus.getBus().post(new BusEvents.ScheduleAmount(appointment.getPrice()));
    }


    /**
     * Seteamos el evento click del card
     *
     * @param object
     * @param idSwService
     */
    private void setItemClick(SubService object, Switch idSwService) {
        SubserviceDetailDialog customDialog = new SubserviceDetailDialog();
        customDialog.setData(context, object);
        customDialog.setListener(new CustomDialog.ButtonsListener() {
            @Override
            public void onDialogPositiveClick(DialogFragment dialog) {
                if (idSwService.isChecked()) {
                    // TODO: Acá agregar para que se pueda deseleccionar desde el dialog
                } else {
                    idSwService.setChecked(true);
                }
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {

            }
        });
        customDialog.show(((AppointmentReservationActivity) context).getSupportFragmentManager(), "dialog");
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

    public void update(List<SubService> listReviews) {
        this.lista = listReviews;
        this.notifyDataSetChanged();
    }
}
