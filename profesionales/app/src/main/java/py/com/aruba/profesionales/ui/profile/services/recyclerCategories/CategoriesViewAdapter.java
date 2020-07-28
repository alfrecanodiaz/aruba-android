package py.com.aruba.profesionales.ui.profile.services.recyclerCategories;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.realm.Realm;
import py.com.aruba.profesionales.R;
import py.com.aruba.profesionales.data.eventbus.BusEvents;
import py.com.aruba.profesionales.data.eventbus.GlobalBus;
import py.com.aruba.profesionales.data.helpers.request.ManageGeneralRequest;
import py.com.aruba.profesionales.data.models.Category;
import py.com.aruba.profesionales.data.realm.GlobalRealm;

import py.com.aruba.profesionales.ui.profile.services.CategoriesActivity;
import py.com.aruba.profesionales.ui.profile.services.ServicesActivity;
import py.com.aruba.profesionales.utils.Utils;
import py.com.aruba.profesionales.utils.UtilsImage;
import py.com.aruba.profesionales.utils.dialogs.InfoDialog;


public class CategoriesViewAdapter extends RecyclerView.Adapter<CategoriesViewHolder> {
    private static final String TAG = "MenuPlatesViewAdapter";

    // Variables
    private final Context context;
    private List<Category> lista;
    private Realm realm;
    private int height;

    public CategoriesViewAdapter(Context context, List<Category> lista) {
        this.context = context;
        this.lista = lista;
        this.realm = GlobalRealm.getDefault();
    }

    @Override
    public CategoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(context).inflate(R.layout.item_categories_button, null);

        // Obtenemos el alto de cada item, dividiendo el total de items por el alto del recycler
        height = parent.getHeight() / lista.size();
        return new CategoriesViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(final CategoriesViewHolder holder, int position) {
        final Category object = lista.get(holder.getAdapterPosition());
        if(object == null) return;

        holder.tvTitle.setText(object.getDisplay_name());

        holder.swEnabled.setChecked(object.getEnabled());
        holder.swEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Guardamos localmente
            realm.beginTransaction();
            object.setEnabled(isChecked);
            realm.copyToRealmOrUpdate(object);
            realm.commitTransaction();

            // Enviamos al backend los cambios realizados
            ManageGeneralRequest.setActiveCategories(context, realm);

            GlobalBus.getBus().post(new BusEvents.ModelUpdated("profile"));
        });

        holder.rlContent.setOnClickListener(v -> {
            Utils.preventTwoClick(v);
            if (object.getIs_active()) {
                setItemClick(object);
            } else {
                InfoDialog customDialog = new InfoDialog();
                customDialog.setData("Atención", object.getInactive_text());
                customDialog.show(((CategoriesActivity) context).getSupportFragmentManager(), "dialog");
            }
        });


        // Seteamos el alto del item y el color de fondo
        holder.rlContent.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
//        holder.rlContent.setBackgroundColor(Color.parseColor(object.getColor()));
        UtilsImage.loadImageCircle(context, holder.ivIcon, object.getImage_url());
    }

    /**
     * Seteamos el evento click del card
     *
     * @param object
     */
    private void setItemClick(Category object) {
        Intent intent = new Intent(context, ServicesActivity.class);
        intent.putExtra("CATEGORY_ID", object.getId());
        context.startActivity(intent);
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

    public void update(List<Category> listReviews) {
        this.lista = listReviews;
        this.notifyDataSetChanged();
    }
}
