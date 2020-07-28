package py.com.aruba.clientes.ui.appointment.fragments.recyclerSubServices;

import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import py.com.aruba.clientes.R;

public class SubServicesViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvPrice)
    TextView tvPrice;
    @BindView(R.id.tvDetails)
    ImageView tvDetails;
    @BindView(R.id.idSwService)
    Switch idSwService;


    SubServicesViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

