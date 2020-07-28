package py.com.aruba.clientes.ui.promotions.recyclerPromotions;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import py.com.aruba.clientes.R;

public class PromotionsViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivPicture)
    ImageView ivPicture;
//    @BindView(R.id.tvSubTitle) TextView tvSubTitle;
//    @BindView(R.id.ivIcon)
//    ImageView ivIcon;
//    @BindView(R.id.rlContent)
//    RelativeLayout rlContent;

    PromotionsViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

