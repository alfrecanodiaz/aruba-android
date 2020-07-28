package py.com.aruba.clientes.ui.cart.recyclerSchedules;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import py.com.aruba.clientes.R;

public class ScheduleViewHolder extends RecyclerView.ViewHolder {


    @BindView(R.id.item) RelativeLayout item;
    @BindView(R.id.tvAddress) TextView tvAddress;
    @BindView(R.id.tvTypeAddress) TextView tvTypeAddress;
    @BindView(R.id.tvCategory) TextView tvCategory;
    @BindView(R.id.tvSubService) TextView tvSubService;
    @BindView(R.id.tvProfessional) TextView tvProfessional;
    @BindView(R.id.tvDate) TextView tvDate;

    ScheduleViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

