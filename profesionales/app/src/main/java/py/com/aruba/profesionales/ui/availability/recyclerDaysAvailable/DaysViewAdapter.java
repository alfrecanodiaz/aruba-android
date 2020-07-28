package py.com.aruba.profesionales.ui.availability.recyclerDaysAvailable;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import java.util.List;

import io.realm.Realm;
import py.com.aruba.profesionales.R;
import py.com.aruba.profesionales.data.eventbus.BusEvents;
import py.com.aruba.profesionales.data.eventbus.GlobalBus;
import py.com.aruba.profesionales.data.helpers.RestAdapter;
import py.com.aruba.profesionales.data.helpers.request.RequestResponseDataJsonObject;
import py.com.aruba.profesionales.data.models.Appointment;
import py.com.aruba.profesionales.data.models.Schedule;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.data.service.ProfileInterface;
import py.com.aruba.profesionales.utils.alerts.AlertGlobal;
import py.com.aruba.profesionales.utils.dialogs.CustomDialog;
import py.com.aruba.profesionales.utils.loading.UtilsLoading;

public class DaysViewAdapter extends RecyclerView.Adapter<DaysViewHolder> {
    private static final String TAG = "ReviewsViewAdapter";

    // Variables
    private final Context context;
    private final AppCompatActivity activity;
    private List<Schedule> lista;
    private Realm realm;
    private Appointment appointment;
    private RelativeLayout selectedItem;

    public DaysViewAdapter(Context context, List<Schedule> lista, AppCompatActivity activity) {
        this.context = context;
        this.activity = activity;
        this.lista = lista;
        this.realm = GlobalRealm.getDefault();
        appointment = realm.where(Appointment.class).findFirst();
    }

    @Override
    public DaysViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(context).inflate(R.layout.item_list_days, null);
        return new DaysViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(final DaysViewHolder holder, int position) {
        final Schedule object = lista.get(holder.getAdapterPosition());

        holder.tvDayName.setText(String.format("Desde las %s hasta las %s ", object.getStart_hour_string(), object.getEnd_hour_string()));
        holder.ivDelete.setOnClickListener(v -> {
            AlertGlobal.showCustomError(activity, "Atención", "¿Está seguro que quiere borrar este registro?", new CustomDialog.ButtonsListener() {
                @Override
                public void onDialogPositiveClick(DialogFragment dialog) {
                    sendToBackend(object.getId());
                }

                @Override
                public void onDialogNegativeClick(DialogFragment dialog) {

                }
            });
        });
    }

    /**
     * Método para enviar al backend el nuevo horario agendado
     */
    private void sendToBackend(int id) {
        UtilsLoading loading = new UtilsLoading(activity);
        loading.showLoading();
        JsonObject data = new JsonObject();
        data.addProperty("id", id);


        RestAdapter.getClient(activity).create(ProfileInterface.class).schedule_delete(data).enqueue(new RequestResponseDataJsonObject("schedule") {
            @Override
            public void successful(JsonObject object) {

                Realm realm = GlobalRealm.getDefault();
                realm.beginTransaction();
                realm.where(Schedule.class).equalTo("id", id).findFirst().deleteFromRealm();
                realm.commitTransaction();

                GlobalBus.getBus().post(new BusEvents.ModelUpdated("schedules"));
                loading.dismissLoading();
                AlertGlobal.showCustomSuccess(activity, "Excelente", "Eliminado Correctamente!");
            }

            @Override
            public void successful(String msg) {
                Realm realm = GlobalRealm.getDefault();
                realm.beginTransaction();
                realm.where(Schedule.class).equalTo("id", id).findFirst().deleteFromRealm();
                realm.commitTransaction();

                GlobalBus.getBus().post(new BusEvents.ModelUpdated("schedules"));
                loading.dismissLoading();

                AlertGlobal.showCustomSuccess(activity, "Atención", msg);
            }

            @Override
            public void error(String msg) {
                AlertGlobal.showCustomError(activity, "Atención", msg);
            }
        });
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

    public void update(List<Schedule> listReviews) {
        this.lista = listReviews;
        this.notifyDataSetChanged();
    }

    public void removeAll() {
        notifyItemRangeRemoved(0, lista.size() - 1);
    }
}
