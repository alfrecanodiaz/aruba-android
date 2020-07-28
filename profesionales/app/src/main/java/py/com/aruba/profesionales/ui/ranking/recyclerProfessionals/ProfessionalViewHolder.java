package py.com.aruba.profesionales.ui.ranking.recyclerProfessionals;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import py.com.aruba.profesionales.R;


public class ProfessionalViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.tvServices) TextView tvServices;
    @BindView(R.id.tvLikes) TextView tvLikes;
    @BindView(R.id.tvPos) TextView tvPos;
    @BindView(R.id.ivLike) ImageView ivLike;
    @BindView(R.id.ivAvatar) ImageView ivAvatar;
    @BindView(R.id.llItem) LinearLayout llItem;
    @BindView(R.id.ratingBar) RatingBar ratingBar;


    ProfessionalViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

