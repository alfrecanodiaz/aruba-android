package py.com.aruba.clientes.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import jp.wasabeef.recyclerview.adapters.SlideInRightAnimationAdapter;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.eventbus.BusEvents;
import py.com.aruba.clientes.data.eventbus.GlobalBus;
import py.com.aruba.clientes.data.helpers.request.ManageGeneralRequest;
import py.com.aruba.clientes.data.models.Appointment;
import py.com.aruba.clientes.data.models.Category;
import py.com.aruba.clientes.data.models.Professional;
import py.com.aruba.clientes.data.models.Review;
import py.com.aruba.clientes.data.models.User;
import py.com.aruba.clientes.data.models.UserAddress;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.ui.about.AboutActivity;
import py.com.aruba.clientes.ui.about.ContactDialog;
import py.com.aruba.clientes.ui.historical.HistoricalAppointmentsActivity;
import py.com.aruba.clientes.ui.main.dialogs.CovidDialog;
import py.com.aruba.clientes.ui.main.dialogs.ReviewDialog;
import py.com.aruba.clientes.ui.main.recyclerCategories.CategoriesViewAdapter;
import py.com.aruba.clientes.ui.professionals.ProfessionalListActivity;
import py.com.aruba.clientes.ui.profile.ProfileActivity;
import py.com.aruba.clientes.ui.promotions.PromotionsActivity;
import py.com.aruba.clientes.utils.Constants;
import py.com.aruba.clientes.utils.Print;
import py.com.aruba.clientes.utils.SharedPreferencesUtils;
import py.com.aruba.clientes.utils.TimeUtils;
import py.com.aruba.clientes.utils.Utils;
import py.com.aruba.clientes.utils.images.UtilsImage;

import static py.com.aruba.clientes.utils.Utils.loginMessage;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.container)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    Realm realm;

    private Context context;
    private AppCompatActivity activity;
    private CategoriesViewAdapter categoriesViewAdapter;
    private User user;
    private boolean isLogged;
    private boolean can_make_appointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_main);
        // PART1 - All activities must have this settings
        ButterKnife.bind(this);
        context = this;
        activity = this;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        realm = GlobalRealm.getDefault();
        GlobalBus.getBus().register(this);
        // END PART1

        user = realm.where(User.class).findFirst();
        isLogged = Utils.isLogged(context);
        can_make_appointment = user.isCan_make_appointment();
        setupNavigationDrawer();

        // Seteamos los datos del navigation drawer
        setNavigationData();
        new ManageGeneralRequest(this).downloadAll();
        initRecycler();

        UtilsImage.setCurrentDate(context);
        checkAppointmentForReview();

        // Mostramos el formulario del covid, al iniciar la app
        if (!SharedPreferencesUtils.getBoolean(activity, "covid_form")) {
            CovidDialog customDialog = new CovidDialog();
            customDialog.setData(activity);
            customDialog.show((activity).getSupportFragmentManager(), "dialog");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalBus.getBus().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_calendar);

        ImageView ivIcon = item.getActionView().findViewById(R.id.ivIcon);
        TextView tvCount = item.getActionView().findViewById(R.id.tvCount);

        if (isLogged) {
            RealmResults<Appointment> listAppointment = realm.where(Appointment.class)
                    .equalTo("status", Constants.APPOINTMENT_STATUS.CREATED).or()
                    .equalTo("status", Constants.APPOINTMENT_STATUS.IN_PROGRESS).or()
                    .equalTo("status", Constants.APPOINTMENT_STATUS.ARRIVED).findAll();

            tvCount.setText(String.valueOf(listAppointment.size()));
            item.setVisible((listAppointment.size() > 0));
            item.getActionView().setOnClickListener(v -> startActivity(new Intent(context, HistoricalAppointmentsActivity.class)));
        } else {
            item.setVisible(false);
        }

        return true;
    }

    /**
     * Seteamos los datos del menú lateral
     */
    private void setNavigationData() {

        TextView tvClient = navigationView.getHeaderView(0).findViewById(R.id.tvClient);
        TextView tvAddress = navigationView.getHeaderView(0).findViewById(R.id.tvAddress);
        ImageView ivAvatar = navigationView.getHeaderView(0).findViewById(R.id.ivAvatar);

        tvClient.setText(String.format("¡Hola %s!", user.getFirst_name()));
        tvAddress.setText(UserAddress.getDefaultAddressString());
        UtilsImage.loadImageCircle(context, ivAvatar, user.getAvatar(), Constants.PLACEHOLDERS.AVATAR);

        // Si no está logueado, ocultamos el boton de cerrar sesión
        if (!isLogged) {
            Menu menuNav = navigationView.getMenu();
            MenuItem logoutItem = menuNav.findItem(R.id.nav_logout);
            logoutItem.setVisible(false);
        }
    }

    /**
     * Método para inicializar el recycler
     */
    private void initRecycler() {
        RealmResults<Category> listCategoriesRealm = realm.where(Category.class).sort("order").findAll();
        List<Category> listCategories = realm.copyFromRealm(listCategoriesRealm);

        // Recycler View
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        categoriesViewAdapter = new CategoriesViewAdapter(MainActivity.this, listCategories, can_make_appointment, isLogged);
        recyclerView.setAdapter(categoriesViewAdapter);
        recyclerView.setHasFixedSize(true);

        // Custom animation
        recyclerView.setAdapter(new SlideInRightAnimationAdapter(categoriesViewAdapter));
    }

    /**
     * Método para actualizar el recyclerview
     */
    private void updateRecyclerView() {
        RealmResults<Category> listCategoriesRealm = realm.where(Category.class).findAll();
        List<Category> listCategories = realm.copyFromRealm(listCategoriesRealm);
        categoriesViewAdapter.update(listCategories);
    }

    /**
     * Método para iniciar y configurar la navegación lateral
     */
    private void setupNavigationDrawer() {

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            // Deseleccionamos todos los items
            unselectDrawerItems();
            // Seleccionamos el que se clickeo
            item.setChecked(true);

            switch (id) {
                // ### PERFIL
                case R.id.nav_profile:
                    if (!isLogged) {
                        loginMessage(MainActivity.this);
                        break;
                    }

                    startActivity(new Intent(context, ProfileActivity.class));
                    break;
                // ### SERVICIOS
                case R.id.nav_services:
                    if (!isLogged) {
                        loginMessage(MainActivity.this);
                        break;
                    }

                    startActivity(new Intent(context, HistoricalAppointmentsActivity.class));
                    break;
                // ### PROFESIONALES
                case R.id.nav_professionals:
                    if (!isLogged) loginMessage(MainActivity.this);
                    else startActivity(new Intent(context, ProfessionalListActivity.class));
                    break;
                // ### PROMOCIONES
                case R.id.nav_promotions:
                    if (!isLogged) {
                        loginMessage(MainActivity.this);
                        break;
                    }

                    startActivity(new Intent(context, PromotionsActivity.class));
                    break;
                // ### CLIENTE FIEL
//                case R.id.nav_customers:
//                    if (!isLogged) loginMessage(MainActivity.this);
//                    else startActivity(new Intent(context, LoyalCustomerActivity.class));
//                    break;
                // ### CONTACTAR
                case R.id.nav_contact:
                    ContactDialog contactDialog = new ContactDialog();
                    contactDialog.setData(MainActivity.this);
                    contactDialog.show(getSupportFragmentManager(), "dialog");
                    break;
                // ### CERRAR SESION
                case R.id.nav_logout:
                    Utils.logout(MainActivity.this);
                    break;
                case R.id.nav_about:
                    startActivity(new Intent(context, AboutActivity.class));
                    break;
            }

            drawer.closeDrawer(GravityCompat.START);
            return false;
        });
    }

    /**
     * Método para deseleccionar los items del menu lateral
     */
    private void unselectDrawerItems() {
        int size = navigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onModelChange(BusEvents.ModelUpdated event) {
        if ("categories".equals(event.model)) {
            updateRecyclerView();
        }
        if ("profile".equals(event.model)) {
            updateRecyclerView();
        }
    }

    /**
     * Verificamos si el último appointment no se calificó
     */
    private void checkAppointmentForReview() {
        Appointment appointment = realm.where(Appointment.class).sort("backend_id", Sort.DESCENDING).findFirst();
        if (appointment == null) return;

        // Si el ultimo sevicio agendado está en estados completed, buscamos si mi ID como usuario, está dentro de los reviews del profesional que realizó la cita. ¿complicado? well, no queda de otra
        if (appointment.getStatus() == Constants.APPOINTMENT_STATUS.COMPLETED) {
            int professional_id = appointment.getProfessional_id();
            int client_id = user.getId();

            Review review = realm.where(Review.class)
                    .equalTo("professional_id", professional_id)
                    .equalTo("client_id", client_id)
                    .findFirst();

            // Si no hay review donde el profesional haya sido calificado por el usuario logueado, mostramos el dichoso popup para calificar
            if (review == null) {
                Professional professional = realm.where(Professional.class).equalTo("id", professional_id).findFirst();

                ReviewDialog schedule = new ReviewDialog();
                schedule.setData(professional, activity);
                schedule.show(getSupportFragmentManager(), "dialog");
            }
        }
    }

}
