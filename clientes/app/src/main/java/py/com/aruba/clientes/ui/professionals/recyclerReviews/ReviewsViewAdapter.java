package py.com.aruba.clientes.ui.professionals.recyclerReviews;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.realm.Realm;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.models.Appointment;
import py.com.aruba.clientes.data.models.Review;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.utils.Constants;
import py.com.aruba.clientes.utils.images.UtilsImage;

public class ReviewsViewAdapter extends RecyclerView.Adapter<ReviewsViewHolder> {
    private static final String TAG = "ReviewsViewAdapter";

    // Variables
    private final Context context;
    private final Activity activity;
    private List<Review> lista;
    private Realm realm;
    private Appointment appointment;
    private RelativeLayout selectedItem;

    public ReviewsViewAdapter(Context context, List<Review> lista, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.lista = lista;
        this.realm = GlobalRealm.getDefault();
        appointment = realm.where(Appointment.class).findFirst();
    }

    @Override
    public ReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(context).inflate(R.layout.item_list_professional_reviews, null);
        return new ReviewsViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(final ReviewsViewHolder holder, int position) {
        final Review object = lista.get(holder.getAdapterPosition());

        holder.tvComment.setText(object.getComment());
        holder.tvClient.setText(object.getUsername());
        holder.tvDate.setText(object.getCreated_at());
        UtilsImage.loadImageCircle(context, holder.ivAvatar, object.getClient_avatar(), Constants.PLACEHOLDERS.AVATAR);

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

    public void update(List<Review> listReviews) {
        this.lista = listReviews;
        this.notifyDataSetChanged();
    }

    public void removeAll() {
        notifyItemRangeRemoved(0, lista.size() - 1);
    }
}
