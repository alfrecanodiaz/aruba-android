package py.com.aruba.clientes.utils.CustomCalendar.recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import java.util.List;

import py.com.aruba.clientes.R;
import py.com.aruba.clientes.ui.appointment.fragments.Fragment02;
import py.com.aruba.clientes.utils.TimeUtils;

public class CalendarViewAdapter extends RecyclerView.Adapter<CalendarViewHolder> {

    private final Context context;
    private final Fragment02.OnCalendarItemClicked listener;
    private List<JsonObject> lista;
    private int previosClicked = -1;

    public CalendarViewAdapter(Context context, List<JsonObject> lista, Fragment02.OnCalendarItemClicked listener) {
        this.context = context;
        this.lista = lista;
        this.listener = listener;
    }


    @Override
    public CalendarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(context).inflate(R.layout.custom_calendar_row, null);
        return new CalendarViewHolder(layoutView);
    }


    @Override
    public void onBindViewHolder(final CalendarViewHolder holder, int position) {
        JsonObject data = lista.get(holder.getAdapterPosition());

        // Seteamos los datos del calendario
        holder.tvDay.setText(TimeUtils.getWeekDay(data.get("dayWeek").getAsInt()));
        holder.tvDate.setText(String.valueOf(data.get("dayMonth").getAsInt()));

        holder.rlDayBg.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_background_calendar));
        holder.tvDate.setTextColor(ContextCompat.getColor(context, R.color.black200));
        holder.tvDay.setTextColor(ContextCompat.getColor(context, R.color.black200));

        holder.llContent.setOnClickListener(v -> {
            // Si se clickea el mismo, retornamos
            if (previosClicked == holder.getAdapterPosition())  return;

            // Enviamos al listener el item que se clickeo
            listener.onCalendarItemClicked(data);
            this.notifyItemChanged(previosClicked);

            previosClicked = holder.getAdapterPosition();
            holder.rlDayBg.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_background_calendar_active));
            holder.tvDate.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.tvDay.setTextColor(ContextCompat.getColor(context, R.color.green1));
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void update(List<JsonObject> updatedList) {
        this.lista = updatedList;
        this.notifyDataSetChanged();
    }
}
