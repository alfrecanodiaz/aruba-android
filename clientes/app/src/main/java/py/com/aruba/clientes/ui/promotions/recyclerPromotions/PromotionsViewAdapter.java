package py.com.aruba.clientes.ui.promotions.recyclerPromotions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.realm.Realm;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.models.Promotions;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.utils.images.UtilsImage;

public class PromotionsViewAdapter extends RecyclerView.Adapter<PromotionsViewHolder> {
    private static final String TAG = "MenuPlatesViewAdapter";

    // Variables
    private final Context context;
    private List<Promotions> lista;
    private Realm realm;
    private int height;

    public PromotionsViewAdapter(Context context, List<Promotions> lista) {
        this.context = context;
        this.lista = lista;
        this.realm = GlobalRealm.getDefault();
    }

    @Override
    public PromotionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(context).inflate(R.layout.item_promotions, null);

        // Obtenemos el alto de cada item, dividiendo el total de items por el alto del recycler
        height = parent.getHeight() / lista.size();
        return new PromotionsViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(final PromotionsViewHolder holder, int position) {
        final Promotions object = lista.get(holder.getAdapterPosition());
        holder.tvTitle.setText(object.getTitle());
        UtilsImage.loadImage(context, holder.ivPicture, object.getImage_url());

//        // Si la categoría no etá activa, mostramos el texto
//        if(!object.getIs_active()){
//            holder.tvSubTitle.setText(object.getInactive_text());
//            holder.tvSubTitle.setVisibility(View.VISIBLE);
//        }
//
//        holder.rlContent.setOnClickListener(v -> {
//            if(object.getIs_active()){
//                setItemClick(object);
//            }else{
//                CustomLoadingActivity customDialog = new CustomLoadingActivity();
//                customDialog.setData("Atención", "Esta categoría estará habilitada próximamente");
//                customDialog.show(((MainActivity)context).getSupportFragmentManager(), "dialog");
//            }
//        });
//
//
//        // Seteamos el alto del item y el color de fondo
//        holder.rlContent.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
//        holder.rlContent.setBackgroundColor(Color.parseColor(object.getColor()));
    }


    /**
     * Seteamos el evento click del card
     *
     * @param object
     */
    private void setItemClick(Promotions object) {
//        AppointmentPrepareDataDialog schedule = new AppointmentPrepareDataDialog();
//        schedule.setData(object.getDisplay_name(), object.getId());
//        schedule.show(((MainActivity) context).getSupportFragmentManager(), "dialog");
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

    public void update(List<Promotions> listReviews) {
        this.lista = listReviews;
        this.notifyDataSetChanged();
    }
}
