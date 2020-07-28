package py.com.aruba.profesionales.ui.availability.recyclerDaysAvailable;

import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import py.com.aruba.profesionales.R;


public class DaysViewHolder extends RecyclerView.ViewHolder {


    @BindView(R.id.tvDayName)
    TextView tvDayName;
    @BindView(R.id.ivDelete)
    ImageView ivDelete;

    DaysViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

