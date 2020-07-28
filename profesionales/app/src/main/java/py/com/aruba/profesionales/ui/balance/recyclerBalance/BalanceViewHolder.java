package py.com.aruba.profesionales.ui.balance.recyclerBalance;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import py.com.aruba.profesionales.R;

public class BalanceViewHolder extends RecyclerView.ViewHolder {


    @BindView(R.id.ivAvatar) ImageView ivAvatar;
    @BindView(R.id.tvDetail) TextView tvDetail;
    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.tvDay) TextView tvDay;
    @BindView(R.id.tvMonth) TextView tvMonth;

    BalanceViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

