package py.com.aruba.clientes.utils.CustomCalendar.recycler;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import py.com.aruba.clientes.R;

class CalendarViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.rlDayBg)
    RelativeLayout rlDayBg;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.tvDay)
    TextView tvDay;
    @BindView(R.id.llContent)
    LinearLayout llContent;

    CalendarViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

