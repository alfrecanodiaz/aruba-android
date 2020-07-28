package py.com.aruba.profesionales.ui.profile.services.recyclerCategories;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import py.com.aruba.profesionales.R;


public class CategoriesViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.swEnabled)
    Switch swEnabled;
    @BindView(R.id.ivIcon)
    ImageView ivIcon;
    @BindView(R.id.rlContent)
    RelativeLayout rlContent;

    CategoriesViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

