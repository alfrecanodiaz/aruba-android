package py.com.aruba.clientes.ui.appointment.fragments.recyclerProfessionals;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import py.com.aruba.clientes.R;

class ProfessionalViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvRatingCount)
    TextView tvRatingCount;
    @BindView(R.id.tvNoAvailability)
    TextView tvNoAvailability;
    @BindView(R.id.ivAvatar)
    ImageView ivAvatar;
    @BindView(R.id.llItem)
    LinearLayout llItem;
    @BindView(R.id.recyclerViewHours)
    RecyclerView recyclerViewHours;
    @BindView(R.id.ratingBar)
    AppCompatRatingBar ratingBar;


    ProfessionalViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

