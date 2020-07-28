package py.com.aruba.clientes.ui.customer.recyclerSubServicePoints;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.realm.Realm;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.models.SubServicePoints;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.utils.Utils;
import py.com.aruba.clientes.utils.images.UtilsImage;

public class CustomerLoyalViewAdapter extends RecyclerView.Adapter<CustomerLoyalViewHolder> {
    private static final String TAG = "MenuPlatesViewAdapter";

    // Variables
    private final Context context;
    private List<SubServicePoints> lista;
    private Realm realm;
    private int height;

    public CustomerLoyalViewAdapter(Context context, List<SubServicePoints> lista) {
        this.context = context;
        this.lista = lista;
        this.realm = GlobalRealm.getDefault();
    }

    @Override
    public CustomerLoyalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(context).inflate(R.layout.item_subservice_points, null);

        // Obtenemos el alto de cada item, dividiendo el total de items por el alto del recycler
        height = parent.getHeight() / lista.size();
        return new CustomerLoyalViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(final CustomerLoyalViewHolder holder, int position) {
        final SubServicePoints object = lista.get(holder.getAdapterPosition());

        // TODO: Temporal, falta definir esto
        holder.tvTitle.setText(object.getSubService().getDisplay_name());
        holder.tvDescription.setText(object.getPoint() + "pts o (" + Utils.miles(object.getPoint() / 2) + "pts + Gs. " + Utils.miles(object.getSubService().getPrice() / 2) + ")");
        UtilsImage.loadImageCircle(context, holder.ivPicture, object.getSubService().getImage_url());
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

    public void update(List<SubServicePoints> listReviews) {
        this.lista = listReviews;
        this.notifyDataSetChanged();
    }
}
