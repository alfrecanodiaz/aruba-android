package py.com.aruba.clientes.ui.historical.recyclerHistoricalSubServices;

import android.content.Context;
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
import py.com.aruba.clientes.utils.images.UtilsImage;

public class HistoricalSubServiceViewAdapter extends RecyclerView.Adapter<HistoricalSubServiceViewHolder> {
    private static final String TAG = "Historical";

    // Variables
    private final Context context;
    private List<AppointmentDetails> lista;
    Realm realm;

    public HistoricalSubServiceViewAdapter(Context context, List<AppointmentDetails> lista) {
        this.context = context;
        this.lista = lista;
        this.realm = GlobalRealm.getDefault();
    }


    @Override
    public HistoricalSubServiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(context).inflate(R.layout.item_appointment_detail_list, null);
        return new HistoricalSubServiceViewHolder(layoutView);
    }


    @Override
    public void onBindViewHolder(final HistoricalSubServiceViewHolder holder, int position) {
        final AppointmentDetails object = lista.get(holder.getAdapterPosition());

        holder.tvService.setText(object.getTitle());
        holder.tvTime.setText(String.format("Duración apróx. %s.", Appointment.getDurationString(object.getTime())));
        UtilsImage.loadImageCircle(context, holder.ivService, object.getImage_url());
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

    public void update(List<AppointmentDetails> listReviews) {
        this.lista = listReviews;
        this.notifyDataSetChanged();
    }
}
