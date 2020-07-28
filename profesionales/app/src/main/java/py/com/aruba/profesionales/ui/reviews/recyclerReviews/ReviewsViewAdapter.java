package py.com.aruba.profesionales.ui.reviews.recyclerReviews;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.realm.Realm;
import py.com.aruba.profesionales.R;
import py.com.aruba.profesionales.data.models.Review;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.ui.reviews.dialogs.ReviewDialog;

public class ReviewsViewAdapter extends RecyclerView.Adapter<ReviewsViewHolder> {
    private static final String TAG = "ReviewsViewAdapter";

    // Variables
    private final Context context;
    private final AppCompatActivity activity;
    private List<Review> lista;
    private Realm realm;

    public ReviewsViewAdapter(Context context, List<Review> lista, AppCompatActivity activity) {
        this.context = context;
        this.activity = activity;
        this.lista = lista;
        this.realm = GlobalRealm.getDefault();
    }

    @Override
    public ReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(context).inflate(R.layout.item_reviews_list, null);
        return new ReviewsViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(final ReviewsViewHolder holder, int position) {
        final Review object = lista.get(holder.getAdapterPosition());

        holder.ratingBar.setRating(object.getCalification());
        holder.tvClient.setText(object.getUsername());
        holder.tvDate.setText(object.getCreated_at());

        holder.llItem.setOnClickListener(v -> {
            ReviewDialog rd = new ReviewDialog();
            rd.setData(activity, object);
            rd.show((activity.getSupportFragmentManager()), "dialog");
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

    public void update(List<Review> listReviews) {
        this.lista = listReviews;
        this.notifyDataSetChanged();
    }
}
