package py.com.aruba.clientes.ui.appointment.fragments.recyclerAppointments;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import py.com.aruba.clientes.R;

public class AppointmentViewHolder extends RecyclerView.ViewHolder {

    //    @BindView(R.id.expandableLayout) ExpandableRelativeLayout expandableLayout;
    @BindView(R.id.rlContentTitle)
    RelativeLayout rlContentTitle;
    @BindView(R.id.item)
    RelativeLayout item;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivDelete)
    ImageView ivDelete;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.tvTypeAddress)
    TextView tvTypeAddress;
    @BindView(R.id.tvCategory)
    TextView tvCategory;
    @BindView(R.id.tvSubService)
    TextView tvSubService;
    @BindView(R.id.tvProfessional)
    TextView tvProfessional;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.ivEditService)
    ImageView ivEditService;
    @BindView(R.id.ivEditProfessional)
    ImageView ivEditProfessional;

    AppointmentViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

