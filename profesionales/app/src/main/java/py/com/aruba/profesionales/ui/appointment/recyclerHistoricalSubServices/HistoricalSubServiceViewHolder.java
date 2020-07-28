package py.com.aruba.profesionales.ui.appointment.recyclerHistoricalSubServices;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import py.com.aruba.profesionales.R;

public class HistoricalSubServiceViewHolder extends RecyclerView.ViewHolder {


    @BindView(R.id.ivService) ImageView ivService;
    @BindView(R.id.tvService) TextView tvService;
    @BindView(R.id.tvTime) TextView tvTime;

    HistoricalSubServiceViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

