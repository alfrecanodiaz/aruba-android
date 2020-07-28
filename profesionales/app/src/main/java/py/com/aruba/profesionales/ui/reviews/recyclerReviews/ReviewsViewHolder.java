package py.com.aruba.profesionales.ui.reviews.recyclerReviews;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import py.com.aruba.profesionales.R;

class ReviewsViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tvClient) TextView tvClient;
    @BindView(R.id.tvDate) TextView tvDate;
    @BindView(R.id.ratingBar) AppCompatRatingBar ratingBar;
    @BindView(R.id.llItem) LinearLayout llItem;

    ReviewsViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

