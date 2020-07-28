package py.com.aruba.profesionales.ui.ranking.recyclerProfessionals;


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
import py.com.aruba.profesionales.data.models.Appointment;
import py.com.aruba.profesionales.data.models.Professional;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.utils.UtilsImage;

public class ProfessionalViewAdapter extends RecyclerView.Adapter<ProfessionalViewHolder> {
    private static final String TAG = "ReviewsViewAdapter";

    // Variables
    private final Context context;
    private final Activity activity;
    private List<Professional> lista;
    private Realm realm;

    public ProfessionalViewAdapter(Context context, List<Professional> lista, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.lista = lista;
        this.realm = GlobalRealm.getDefault();
    }

    @Override
    public ProfessionalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(context).inflate(R.layout.item_list_professional, null);
        return new ProfessionalViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(final ProfessionalViewHolder holder, int position) {
        final Professional object = lista.get(holder.getAdapterPosition());

        holder.tvPos.setText(String.valueOf(position+1));
        holder.tvName.setText(String.format("%s %s", object.getFirst_name(), object.getLast_name()));
        holder.tvLikes.setText(String.valueOf(object.getLikes_count()));
        holder.tvServices.setText(String.valueOf(object.getServices_count()));
        holder.ratingBar.setNumStars(Integer.parseInt(object.getAverage_reviews()));

        UtilsImage.loadImageCircle(context, holder.ivAvatar, object.getAvatar());
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

    public void removeAll() {
        notifyItemRangeRemoved(0, lista.size() - 1);
    }
}
