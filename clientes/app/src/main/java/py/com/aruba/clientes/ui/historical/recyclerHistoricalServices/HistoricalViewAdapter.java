package py.com.aruba.clientes.ui.historical.recyclerHistoricalServices;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.realm.Realm;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.models.Appointment;
import py.com.aruba.clientes.data.models.AppointmentDetails;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.ui.historical.HistoricalAppointmentDetailsActivity;
import py.com.aruba.clientes.utils.TimeUtils;

public class HistoricalViewAdapter extends RecyclerView.Adapter<HistoricalViewHolder> {
    private static final String TAG = "Historical";

    // Variables
    private final Context context;
    private List<Appointment> lista;
    Realm realm;

    public HistoricalViewAdapter(Context context, List<Appointment> lista) {
        this.context = context;
        this.lista = lista;
        this.realm = GlobalRealm.getDefault();
    }


    @Override
    public HistoricalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(context).inflate(R.layout.item_historical_services, null);
        return new HistoricalViewHolder(layoutView);
    }


    @Override
    public void onBindViewHolder(final HistoricalViewHolder holder, int position) {
        final Appointment object = lista.get(holder.getAdapterPosition());
        holder.rlData.setOnClickListener(v -> setItemClick(object));
        holder.tvCategory.setText(getCategoryName(object));
        holder.tvDetail.setText(getDetailText(object));
        holder.tvStatus.setText(Appointment.getStatusString(object.getStatus()));
    }

    /**
     * Método para retornar el nombre de la categoría del servicio
     * @param object
     * @return
     */
    private String getCategoryName(Appointment object) {
        String category = "Servicio";
        AppointmentDetails ad = realm.where(AppointmentDetails.class).equalTo("appointment.id", object.getId()).findFirst();
        if(ad != null && ad.getCategory() != null){
            category = ad.getCategory().getDisplay_name();
        }
        return category;
    }

    /**
     * Método para retornar el texto formateado del detalle de un appointment
     * @param object
     * @return
     */
    private String getDetailText(Appointment object) {
        return String.format("%s | %s", TimeUtils.getDateStringReadable(object.getDate()), object.getHour_start_pretty());
    }


    /**
     * Seteamos el evento click del card
     *
     * @param object
     */
    private void setItemClick(Appointment object) {
        Intent details = new Intent(context, HistoricalAppointmentDetailsActivity.class);
        details.putExtra("ID", object.getBackend_id());
        context.startActivity(details);
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
