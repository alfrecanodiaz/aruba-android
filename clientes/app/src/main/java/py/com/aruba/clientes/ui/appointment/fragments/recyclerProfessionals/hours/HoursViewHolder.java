package py.com.aruba.clientes.ui.appointment.fragments.recyclerProfessionals.hours;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import py.com.aruba.clientes.R;

class HoursViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.rlButton)
    RelativeLayout rlButton;
    @BindView(R.id.tvHour)
    TextView tvHour;

    HoursViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

