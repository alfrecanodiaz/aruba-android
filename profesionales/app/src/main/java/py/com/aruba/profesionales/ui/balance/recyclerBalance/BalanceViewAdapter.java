package py.com.aruba.profesionales.ui.balance.recyclerBalance;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import py.com.aruba.profesionales.R;
import py.com.aruba.profesionales.data.models.Appointment;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.utils.Utils;
import py.com.aruba.profesionales.utils.UtilsImage;

public class BalanceViewAdapter extends RecyclerView.Adapter<BalanceViewHolder> {
    private static final String TAG = "BalanceViewAdapter";

    // Variables
    private final AppCompatActivity activity;
    private List<Appointment> lista;
    Realm realm;

    public BalanceViewAdapter(List<Appointment> lista, AppCompatActivity activity) {
        this.lista = lista;
        this.activity = activity;
        this.realm = GlobalRealm.getDefault();
    }


    @Override
    public BalanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(activity).inflate(R.layout.item_balance_detail, null);
        return new BalanceViewHolder(layoutView);
    }


    @Override
    public void onBindViewHolder(final BalanceViewHolder holder, int position) {
        final Appointment object = lista.get(holder.getAdapterPosition());

        holder.tvName.setText(object.getClient());
        holder.tvDetail.setText(String.format("%s - %s", object.getPayment_method(), Utils.priceFormat(object.getProfessional_earnings())));

        holder.tvDay.setText(DateFormat.format("dd", object.getDate()));
        String month = String.valueOf(DateFormat.format("MMM", object.getDate()));
        holder.tvMonth.setText(month.replace(".",""));
        UtilsImage.loadImageCircle(activity, holder.ivAvatar, object.getClient_avatar());
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

    public void update(List<Appointment> listReviews) {
        this.lista = listReviews;
        this.notifyDataSetChanged();
    }
}
