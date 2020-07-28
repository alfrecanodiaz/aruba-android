package py.com.aruba.clientes.ui.professionals.recyclerReviews;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import py.com.aruba.clientes.R;

public class ReviewsViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tvComment)
    TextView tvComment;
    @BindView(R.id.tvClient)
    TextView tvClient;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.ivAvatar)
    ImageView ivAvatar;

    ReviewsViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

