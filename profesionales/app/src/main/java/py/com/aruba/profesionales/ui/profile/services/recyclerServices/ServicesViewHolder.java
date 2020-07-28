package py.com.aruba.profesionales.ui.profile.services.recyclerServices;

import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import py.com.aruba.profesionales.R;


public class ServicesViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.swService) Switch swService;
    @BindView(R.id.ivPicture) ImageView ivPicture;

    ServicesViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

