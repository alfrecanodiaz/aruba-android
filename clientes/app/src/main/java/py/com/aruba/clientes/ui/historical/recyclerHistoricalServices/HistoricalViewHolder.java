package py.com.aruba.clientes.ui.historical.recyclerHistoricalServices;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import py.com.aruba.clientes.R;

public class HistoricalViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.rlData) RelativeLayout rlData;
    @BindView(R.id.tvCategory) TextView tvCategory;
    @BindView(R.id.tvDetail) TextView tvDetail;
    @BindView(R.id.tvStatus) TextView tvStatus;

    HistoricalViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

