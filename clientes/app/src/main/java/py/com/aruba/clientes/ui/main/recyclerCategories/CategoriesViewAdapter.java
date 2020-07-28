package py.com.aruba.clientes.ui.main.recyclerCategories;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.realm.Realm;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.models.Category;
import py.com.aruba.clientes.data.models.User;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.ui.main.MainActivity;
import py.com.aruba.clientes.ui.main.dialogs.AppointmentPrepareDataDialog;
import py.com.aruba.clientes.ui.main.dialogs.CovidDialog;
import py.com.aruba.clientes.utils.SharedPreferencesUtils;
import py.com.aruba.clientes.utils.Utils;
import py.com.aruba.clientes.utils.UtilsNetwork;
import py.com.aruba.clientes.utils.alerts.AlertGlobal;
import py.com.aruba.clientes.utils.dialogs.InfoDialog;
import py.com.aruba.clientes.utils.images.UtilsImage;

import static py.com.aruba.clientes.utils.Utils.askForPhone;
import static py.com.aruba.clientes.utils.Utils.disabledUserMessage;
import static py.com.aruba.clientes.utils.Utils.loginMessage;

public class CategoriesViewAdapter extends RecyclerView.Adapter<CategoriesViewHolder> {
    private static final String TAG = "MenuPlatesViewAdapter";

    // Variables
    private final AppCompatActivity activity;
    private boolean can_make_appointment;
    private boolean isLogged;
    private List<Category> lista;
    private Realm realm;
    private int height;

    public CategoriesViewAdapter(AppCompatActivity activity, List<Category> lista, boolean can_make_appointment, boolean isLogged) {
        this.activity = activity;
        this.lista = lista;
        this.can_make_appointment = can_make_appointment;
        this.isLogged = isLogged;
        this.realm = GlobalRealm.getDefault();
    }

    @Override
    public CategoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(activity).inflate(R.layout.item_categories_button, null);

        // Obtenemos el alto de cada item, dividiendo el total de items por el alto del recycler
        height = parent.getHeight() / lista.size();
        return new CategoriesViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(final CategoriesViewHolder holder, int position) {
        final Category object = lista.get(holder.getAdapterPosition());
        holder.tvTitle.setText(object.getDisplay_name());

        // Si la categoría no etá activa, mostramos el texto
        if (!object.getIs_active()) {
            holder.tvSubTitle.setText(object.getInactive_text());
            holder.tvSubTitle.setVisibility(View.VISIBLE);
        }

        holder.rlContent.setOnClickListener(v -> {
            Utils.preventTwoClick(v);

            // Primero verificamos si el usuario está logueado
            if (!isLogged) {
                loginMessage(activity);
                return;
            }

            // Ahora verificamos si el usuario marcó el formulario para covid
            if(!SharedPreferencesUtils.getBoolean(activity, "covid_form")){
                CovidDialog customDialog = new CovidDialog();
                customDialog.setData(activity);
                customDialog.show((activity).getSupportFragmentManager(), "dialog");
                return;
            }

            // Verificamos si el usuario puede realizar un appoinment
            if (!can_make_appointment) {
                disabledUserMessage(activity);
                return;
            }

            // Verificamos si el usuario tiene guardado su nro de teléfono
            if (User.getUser(realm).getPhone() == null || User.getUser(realm).getPhone().isEmpty()) {
                askForPhone(activity);
                return;
            }

            if (object.getIs_active()) {
                setItemClick(object);
            } else {
                InfoDialog customDialog = new InfoDialog();
                customDialog.setData("Atención", object.getInactive_text());
                customDialog.show((activity).getSupportFragmentManager(), "dialog");
            }
        });


        // Seteamos el alto del item y el color de fondo
        holder.rlContent.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        holder.rlContent.setBackgroundColor(Color.parseColor(object.getColor()));
        UtilsImage.loadImageCircleNoCache(activity, holder.ivIcon, object.getImage_url());
    }

    /**
     * Seteamos el evento click del card
     *
     * @param object
     */
    private void setItemClick(Category object) {
        if (!Utils.isLogged(activity)) {
            loginMessage((activity));
            return;
        }
        if (!UtilsNetwork.isNetworkAvailable(activity)) {
            AlertGlobal.showCustomError((activity), "Atención", "Para hacer uso de la app necesita conexión a internet");
            return;
        }
        AppointmentPrepareDataDialog schedule = new AppointmentPrepareDataDialog();
        schedule.setData(object.getDisplay_name(), object.getId());
        schedule.show((activity).getSupportFragmentManager(), "dialog");
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
        User user = realm.where(User.class).findFirst();
        can_make_appointment = user.isCan_make_appointment();

        this.lista = listReviews;
        this.notifyDataSetChanged();
    }
}
