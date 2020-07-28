package py.com.aruba.clientes.ui.appointment.fragments.recyclerProfessionals.hours;

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

public class HoursViewAdapter extends RecyclerView.Adapter<HoursViewHolder> {

    private final Context context;
    private final Fragment02.OnHourItemClicked listener;
    private List<JsonObject> lista;
    private int previosClicked = -1;

    public HoursViewAdapter(Context context, List<JsonObject> lista, Fragment02.OnHourItemClicked listener) {
        this.context = context;
        this.lista = lista;
        this.listener = listener;
    }


    @Override
    public HoursViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(context).inflate(R.layout.item_professional_hours, null);
        return new HoursViewHolder(layoutView);
    }


    @Override
    public void onBindViewHolder(final HoursViewHolder holder, int position) {
        JsonObject data = lista.get(holder.getAdapterPosition());

        // Establecemos el horario y luego el diseño
        holder.tvHour.setText(data.get("readable_hour").getAsString());

        holder.rlButton.setBackground(ContextCompat.getDrawable(context, R.drawable.hours_just_border));
        holder.tvHour.setTextColor(ContextCompat.getColor(context, R.color.blackInfo));

        holder.rlButton.setOnClickListener(v -> {
            // Si se clickea el mismo, retornamos
            if (previosClicked == holder.getAdapterPosition())  return;

            // Enviamos al listener el item que se clickeo
            listener.onHourItemClicked(data);
            this.notifyItemChanged(previosClicked);

            previosClicked = holder.getAdapterPosition();
            holder.rlButton.setBackground(ContextCompat.getDrawable(context, R.drawable.hours_green_full));
            holder.tvHour.setTextColor(ContextCompat.getColor(context, R.color.white));
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
