package py.com.aruba.profesionales.ui.profile.services.recyclerServices;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.realm.Realm;
import py.com.aruba.profesionales.R;
import py.com.aruba.profesionales.data.helpers.request.ManageGeneralRequest;
import py.com.aruba.profesionales.data.models.Appointment;
import py.com.aruba.profesionales.data.models.SubService;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.ui.profile.services.ServicesActivity;
import py.com.aruba.profesionales.utils.UtilsImage;
import py.com.aruba.profesionales.utils.alerts.AlertGlobal;

public class ServicesViewAdapter extends RecyclerView.Adapter<ServicesViewHolder> {
    private static final String TAG = "ReviewsViewAdapter";

    // Variables
    private final Context context;
    private final Activity activity;
    private List<SubService> lista;
    private Realm realm;
    private Appointment appointment;
    private RelativeLayout selectedItem;

    public ServicesViewAdapter(Context context, List<SubService> lista, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.lista = lista;
        this.realm = GlobalRealm.getDefault();
        appointment = realm.where(Appointment.class).findFirst();
    }

    @Override
    public ServicesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(context).inflate(R.layout.item_list_service, null);
        return new ServicesViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(final ServicesViewHolder holder, int position) {
        final SubService object = lista.get(holder.getAdapterPosition());
        holder.swService.setText(object.getDisplay_name());
        holder.swService.setChecked(object.getEnabled());

        holder.swService.setOnCheckedChangeListener((buttonView, isChecked) -> {
            buttonView.setChecked(!isChecked);
            AlertGlobal.showCustomError((ServicesActivity)context, "Atención", "Para poder habilitar o deshabilitar servicios, por favor contacte con la administración.");
//            // Guardamos localmente
//            realm.beginTransaction();
//            object.setEnabled(isChecked);
//            realm.copyToRealmOrUpdate(object);
//            realm.commitTransaction();
            // Enviamos al backend los cambios realizados
//            ManageGeneralRequest.setActiveServices(context, realm);
        });

        UtilsImage.loadImageCircle(context, holder.ivPicture, object.getImage_url());
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

    public void removeAll() {
        notifyItemRangeRemoved(0, lista.size() - 1);
    }
}
