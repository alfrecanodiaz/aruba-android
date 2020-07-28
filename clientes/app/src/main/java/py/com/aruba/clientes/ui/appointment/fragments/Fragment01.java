package py.com.aruba.clientes.ui.appointment.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionInflater;
import androidx.transition.TransitionManager;

import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import jp.wasabeef.recyclerview.adapters.SlideInRightAnimationAdapter;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.eventbus.BusEvents;
import py.com.aruba.clientes.data.eventbus.GlobalBus;
import py.com.aruba.clientes.data.models.Appointment;
import py.com.aruba.clientes.data.models.Professional;
import py.com.aruba.clientes.data.models.SubCategory;
import py.com.aruba.clientes.data.models.SubService;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.ui.appointment.AppointmentReservationActivity;
import py.com.aruba.clientes.ui.appointment.fragments.recyclerSubServices.SubServicesViewAdapter;
import py.com.aruba.clientes.utils.Print;
import py.com.aruba.clientes.utils.Utils;

public class Fragment01 extends Fragment {

    @BindView(R.id.llSubcategoryButtons) LinearLayout llSubcategoryButtons;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    private List<SubCategory> listSubCategories;
    private Realm realm;
    private AppointmentReservationActivity activity;
    private SubServicesViewAdapter subServicesViewAdapter;
    private Appointment appointment;
    private List<Button> listBtnService;
    public boolean canSwipe;
    public String message = "Por favor seleccione un servicio para continuar";
    private String query = "";
    private List<SubService> listSubServices;
    private int subCategoryID;

    public static Fragment01 newInstance() {
        return new Fragment01();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_schedule_01_services, container, false);
        ButterKnife.bind(this, view);
        realm = GlobalRealm.getDefault();
        activity = (AppointmentReservationActivity) getActivity();
        listBtnService = new ArrayList<>();


        // Obtenemos el appointment activo
        appointment = realm.where(Appointment.class).equalTo("id", activity.APPOINTMENT_ID).findFirst();

        listSubCategories = realm.where(SubCategory.class)
                .equalTo("category.id", appointment.getCategory_id())
                .equalTo("client_type", appointment.getClient_type())
                .sort("order")
                .findAll();

        initRecycler(appointment.getCategory_id());
        setSubCategoryButtons();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        GlobalBus.getBus().register(this);
        super.onAttach(context);
    }

    /**
     * Método para setear los botones de sub-categorias
     */
    private void setSubCategoryButtons() {
        if (listSubCategories.size() == 0) return;

        llSubcategoryButtons.removeAllViews();
        // Asociamos el view al servicio
        for (SubCategory subCategory : listSubCategories) {
            final View view = LayoutInflater.from(activity).inflate(R.layout.item_subcategories_button, null);
            Button btnService = view.findViewById(R.id.btnService);

            btnService.setText(subCategory.getDisplay_name());
            listBtnService.add(btnService);

            // Seteamos el button por default y el clickeado
            btnService.setOnClickListener(v -> {
                subCategoryID = subCategory.getId();
                setButtonStatus(btnService);
                filterDataBase(subCategoryID);
                updateRecyclerView();
            });

            // Agregamos la vista al contenedor padre
            llSubcategoryButtons.addView(view);
        }

        // Setamos el primer item como "selected"
        setButtonStatus(listBtnService.get(0));
        filterDataBase(listSubCategories.get(0).getId());
        updateRecyclerView();
    }

    /**
     * Establecemso el onclick de los botones de sub-categorias
     * @param btnService
     */
    private void setButtonStatus(Button btnService) {
        // Reiniciamos los colores de los demás botones y seteamos el actual
        for (Button b : listBtnService) {
            b.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_empty));
            b.setTextColor(ContextCompat.getColor(activity, R.color.green1));
        }

        btnService.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_aqua));
        btnService.setTextColor(ContextCompat.getColor(activity, R.color.white));
    }

    /**
     * Cargamos en memoria los datos desde la BD
     */
    private void filterDataBase(int _subCategoryID) {
        RealmResults<SubService> listSubServicesRealm = realm.where(SubService.class).equalTo("subCategory.id", _subCategoryID).findAll();

        // Filtramos si es que el query no está vacio
        if (!query.isEmpty()) {
            listSubServicesRealm = listSubServicesRealm.where()
                    .contains("display_name", query, Case.INSENSITIVE)
                    .findAll();
        }
        listSubServices = realm.copyFromRealm(listSubServicesRealm);
    }

    /**
     * Método para actualizar el recyclerview de los servicios
     */
    private void updateRecyclerView() {
        subServicesViewAdapter.update(listSubServices);
    }

    /**
     * Método para inicializar el recycler
     */
    private void initRecycler(int category_id) {

        // Obtenemos todos los sub-servicios asociados a la categoría
        RealmResults<SubService> listSubServicesRealm = realm.where(SubService.class).equalTo("subCategory.category.id", category_id).findAll();
        listSubServices = realm.copyFromRealm(listSubServicesRealm);

        // Recycler View
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(mLayoutManager);
        subServicesViewAdapter = new SubServicesViewAdapter(activity, listSubServices, appointment, this);
        recyclerView.setAdapter(subServicesViewAdapter);

        // Custom animation
        recyclerView.setAdapter(new SlideInRightAnimationAdapter(subServicesViewAdapter));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onModelChange(BusEvents.ModelUpdated event) {
        if ("services".equals(event.model)) {
            query = "";
            filterDataBase(subCategoryID);
            updateRecyclerView();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onQueryChange(BusEvents.ScheduleSearchQueryServices event) {
        this.query = event.string;
        filterDataBase(subCategoryID);
        updateRecyclerView();
    }
}