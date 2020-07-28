package py.com.aruba.profesionales.ui.appointment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import jp.wasabeef.recyclerview.adapters.SlideInRightAnimationAdapter;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import py.com.aruba.profesionales.R;
import py.com.aruba.profesionales.data.eventbus.BusEvents;
import py.com.aruba.profesionales.data.eventbus.GlobalBus;
import py.com.aruba.profesionales.data.helpers.RestAdapter;
import py.com.aruba.profesionales.data.helpers.request.RequestResponseDataJsonObject;
import py.com.aruba.profesionales.data.models.Appointment;
import py.com.aruba.profesionales.data.models.AppointmentDetails;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.data.service.AppointmentInterface;
import py.com.aruba.profesionales.ui.appointment.counter.AppointmentServiceCountdownActivity;
import py.com.aruba.profesionales.ui.appointment.dialog.CancelServiceDialog;
import py.com.aruba.profesionales.ui.appointment.recyclerHistoricalSubServices.HistoricalSubServiceViewAdapter;
import py.com.aruba.profesionales.utils.Constants;
import py.com.aruba.profesionales.utils.TimeUtils;
import py.com.aruba.profesionales.utils.Utils;
import py.com.aruba.profesionales.utils.UtilsImage;
import py.com.aruba.profesionales.utils.alerts.AlertGlobal;
import py.com.aruba.profesionales.utils.loading.UtilsLoading;

public class AppointmentDetailsActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int ACCESS_FINE_LOCATION = 123;

    @BindView(R.id.btnArrived)
    Button btnArrived;
    @BindView(R.id.btnSendMessage)
    Button btnSendMessage;
    @BindView(R.id.btnCancelAppointment)
    Button btnCancelAppointment;
    @BindView(R.id.tvStatus)
    TextView tvStatus;
    @BindView(R.id.tvClient)
    TextView tvClient;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.tvHour)
    TextView tvHour;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.tvTotal)
    TextView tvTotal;
    @BindView(R.id.tvPaymentMethod)
    TextView tvPaymentMethod;
    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.ivAvatar)
    ImageView ivAvatar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private AppCompatActivity activity;
    private Realm realm;
    private Appointment appointment;
    private Location myLocation;
    String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private UtilsLoading loadingUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);
        ButterKnife.bind(this);
        activity = this;
        GlobalBus.getBus().register(this);
        realm = GlobalRealm.getDefault();
        int appointment_id = getIntent().getIntExtra("ID", 0);
        appointment = Appointment.getByBackendID(appointment_id);
        loadingUtils = new UtilsLoading(this);
        setData();
        initRecycler();
    }

    /**
     * Método para setear los datos de las pantallas
     */
    private void setData() {
        UtilsImage.loadImageCircle(activity, ivAvatar, appointment.getClient_avatar());
        tvClient.setText(String.format("Cliente: %s", appointment.getClient()));
        tvDate.setText(String.format("Fecha: %s", TimeUtils.getDateStringReadable(appointment.getDate())));
        tvHour.setText(String.format("Hora: %s", appointment.getHour_start_pretty()));
        tvTotal.setText(String.format("Total: %s", Utils.priceFormat(appointment.getAmount())));
        tvTime.setText(String.format("Tiempo estimado: %s", Appointment.getDurationString(appointment.getDuration())));
        tvAddress.setText(String.format("%s", appointment.getAddress()));

        String payment = appointment.getPayment_method();
        if (payment.equals("Efectivo")) {
            tvPaymentMethod.setText(String.format("Método de Pago: %s (cambio de %s)", payment, Utils.priceFormat(appointment.getClient_amount())));
        } else {
            tvPaymentMethod.setText(String.format("Método de Pago: %s", payment));
        }

        // Color del borde del appointment
        if (appointment.getStatus() == Constants.APPOINTMENT_STATUS.CANCELED) {
            tvStatus.setBackgroundColor(getResources().getColor(R.color.brown2));
            tvStatus.setText("Cita cancelada");
        } else if (appointment.getStatus() == Constants.APPOINTMENT_STATUS.COMPLETED) {
            tvStatus.setBackgroundColor(getResources().getColor(R.color.aquamarineBackground));
            tvStatus.setText("Cita concretada");
        } else {

            if (appointment.getStatus() == Constants.APPOINTMENT_STATUS.ARRIVED) {
                tvStatus.setText("Cita en puerta");
            }
            if (appointment.getStatus() == Constants.APPOINTMENT_STATUS.IN_PROGRESS) {
                tvStatus.setText("Cita en progreso");
            }
            if (appointment.getStatus() == Constants.APPOINTMENT_STATUS.CREATED) {

            }
        }

        setButtonStatus();
    }

    /**
     * Seteamos los estados de los botones
     */
    private void setButtonStatus() {
        switch (appointment.getStatus()) {
            case Constants.APPOINTMENT_STATUS.CREATED:
                tvStatus.setText("Cita pendiente");
                tvStatus.setBackgroundColor(getResources().getColor(R.color.brown1));
                btnArrived.setText("YA LLEGUE");
                btnCancelAppointment.setText("CANCELAR CITA");
                btnSendMessage.setVisibility(View.GONE);
                break;
            case Constants.APPOINTMENT_STATUS.ARRIVED:
                tvStatus.setText("Cita en puerta");
                tvStatus.setBackgroundColor(getResources().getColor(R.color.brown1));
                btnArrived.setText("EMPEZAR SERVICIO");
                btnCancelAppointment.setVisibility(View.GONE);
                break;
            case Constants.APPOINTMENT_STATUS.IN_PROGRESS:
                tvStatus.setText("Cita en progreso");
                tvStatus.setBackgroundColor(getResources().getColor(R.color.brown1));
                btnArrived.setText("VER RELOJ");
                btnSendMessage.setVisibility(View.GONE);
                btnCancelAppointment.setVisibility(View.GONE);
                break;
            case Constants.APPOINTMENT_STATUS.COMPLETED:
                tvStatus.setText("Cita concretada");
                tvStatus.setBackgroundColor(getResources().getColor(R.color.aquamarineBackground));
                btnArrived.setVisibility(View.GONE);
                btnSendMessage.setVisibility(View.GONE);
                btnCancelAppointment.setVisibility(View.GONE);
                break;
            case Constants.APPOINTMENT_STATUS.CANCELED:
                tvStatus.setText("Cita cancelada");
                tvStatus.setBackgroundColor(getResources().getColor(R.color.brown2));
                btnArrived.setVisibility(View.GONE);
                btnSendMessage.setVisibility(View.GONE);
                btnCancelAppointment.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * Inicializamos el recycler
     */
    private void initRecycler() {

        RealmResults<AppointmentDetails> listAppointmentRealm = realm.where(AppointmentDetails.class)
                .equalTo("appointment.id", appointment.getId()).findAll();

        List<AppointmentDetails> listAppointments = realm.copyFromRealm(listAppointmentRealm);

        // Recycler View
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(mLayoutManager);
        HistoricalSubServiceViewAdapter historicalSubServiceViewAdapter = new HistoricalSubServiceViewAdapter(activity, listAppointments);
        recyclerView.setAdapter(historicalSubServiceViewAdapter);

        // Custom animation
        recyclerView.setAdapter(new SlideInRightAnimationAdapter(historicalSubServiceViewAdapter));
    }

    /**
     * Verificamos si el GPS está encendido y si se puede enviar la ubicación del usuario
     */
    private void checkGPS() {
        // Solamente controlamos el GPS en el estado CREATED, porque el botón "ya llegué" solo se muestra en este estado
        if (appointment.getStatus() != Constants.APPOINTMENT_STATUS.CREATED) return;

        // Si tiene los permisos del GPS
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Si el GPS está encendido
            if (isLocationEnabled()) {
                getLocation();
            } else {
                buildAlertMessageNoGps();
            }
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.gps_permission), ACCESS_FINE_LOCATION, perms);
        }

    }

    /**
     * Método para solicitar encender el GPS
     */
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El GPS del teléfono está deshabilitado ¿quieres habilitarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", (dialog, id) -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Obtenemos la ubicación del usuario a través de GoogleServicesLocation.. We can do with LocationManager, but this way is faster and less code :P
     */
    private void getLocation() {
        // TODO: A veces location is always null
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        myLocation = location;

                        changeAppointmentToArrived();
                    } else {
                        AlertGlobal.showCustomError(activity, "Atención", "No se pudo obtener la ubicación, por favor intente de nuevo.");
                    }
                });
    }

    /**
     * Verificamos si el GPS está encendido
     *
     * @return
     */
    private Boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @OnClick(R.id.rlBackButton)
    public void onBack() {
        onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Volvemos a llamar a los métodos de location cuando vuelve de la pantalla de settings
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            checkGPS();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode == ACCESS_FINE_LOCATION) {
            checkGPS();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }

    @OnClick({R.id.btnArrived, R.id.btnCancelAppointment, R.id.btnSendMessage, R.id.ivGmaps})
    public void onViewClicked(View view) {
        Utils.preventTwoClick(view);
        switch (view.getId()) {
            case R.id.btnArrived:
                // Si está en "ya llegué", entonces marcamos in progress
                switch (appointment.getStatus()) {
                    case Constants.APPOINTMENT_STATUS.CREATED:
                        if (!canClickedArrived()) return;
                        checkGPS();
                        break;
                    case Constants.APPOINTMENT_STATUS.ARRIVED:
                        changeAppointmentToInProgress();
                        break;
                    case Constants.APPOINTMENT_STATUS.IN_PROGRESS:
                        // Abrimos el activity del counter
                        Intent intent = new Intent(AppointmentDetailsActivity.this, AppointmentServiceCountdownActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("ID", appointment.getBackend_id());
                        startActivity(intent);
                        AppointmentDetailsActivity.this.finish();
                        break;
                }
                break;
            case R.id.btnCancelAppointment:
                CancelServiceDialog cancelService = new CancelServiceDialog();
                cancelService.setData(appointment, activity);
                cancelService.show(getSupportFragmentManager(), "dialog");
                break;
            case R.id.btnSendMessage:
                if (appointment.getClient_phone().isEmpty()) {
                    AlertGlobal.showCustomError(activity, "Atención", "El cliente no guardó su número de celular");
                } else {
                    String url = "https://api.whatsapp.com/send?phone=" + appointment.getClient_phone();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
                break;
            case R.id.ivGmaps:
                Uri gmmIntentUri = Uri.parse(String.format("geo:0,0?q=%s,%s(%s)", appointment.getLat(), appointment.getLng(), appointment.getClient()));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    AlertGlobal.showCustomError(activity, "Atención", "Por favor instale la aplicación de google maps para poder ver la ubicación.");
                }
                break;
        }
    }

    /**
     * método para cambiar el appointment a "in_progress"
     */
    private void changeAppointmentToInProgress() {
        loadingUtils.showLoading();
        // Preparamos el JSON para enviar la ubi
        JsonObject data = new JsonObject();
        data.addProperty("appointment_id", appointment.getBackend_id());

        // Enviamos al backend el appointment_id y la ubicación del usuario
        RestAdapter.getClient(activity).create(AppointmentInterface.class).set_in_progress(data).enqueue(new RequestResponseDataJsonObject("in_progress") {
            @Override
            public void successful(JsonObject object) {
            }

            @Override
            public void successful(String msg) {
                loadingUtils.dismissLoading();

                realm.beginTransaction();
                appointment.setStatus(Constants.APPOINTMENT_STATUS.IN_PROGRESS);
                appointment.setIn_progress(true);
                appointment.setStart_at(TimeUtils.getActualTimeInSeconds());
                realm.commitTransaction();
                AlertGlobal.showCustomSuccess(activity, "Excelente", msg);
                GlobalBus.getBus().post(new BusEvents.ModelUpdated("appointment"));

                // Abrimos el activity del counter
                Intent intent = new Intent(AppointmentDetailsActivity.this, AppointmentServiceCountdownActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("ID", appointment.getBackend_id());
                startActivity(intent);
                AppointmentDetailsActivity.this.finish();
            }

            @Override
            public void error(String msg) {
                AlertGlobal.showCustomError(activity, "Atención", msg);
                loadingUtils.dismissLoading();
            }
        });
    }

    /**
     * método para cambiar el appointment a "arrived"
     */
    private void changeAppointmentToArrived() {
        loadingUtils.showLoading();
        // Preparamos el JSON para enviar la ubi
        JsonObject data = new JsonObject();
        data.addProperty("lat", myLocation.getLatitude());
        data.addProperty("long", myLocation.getLongitude());
        data.addProperty("appointment_id", appointment.getBackend_id());

        // Enviamos al backend el appointment_id y la ubicación del usuario
        RestAdapter.getClient(activity).create(AppointmentInterface.class).set_arrived(data).enqueue(new RequestResponseDataJsonObject("arrived") {
            @Override
            public void successful(JsonObject object) {
            }

            @Override
            public void successful(String msg) {
                realm.beginTransaction();
                appointment.setStatus(Constants.APPOINTMENT_STATUS.ARRIVED);
                realm.commitTransaction();
                AlertGlobal.showCustomSuccess(activity, "Excelente", msg);
                GlobalBus.getBus().post(new BusEvents.ModelUpdated("appointment"));

                loadingUtils.dismissLoading();
            }

            @Override
            public void error(String msg) {
                AlertGlobal.showCustomError(activity, "Atención", msg);
                loadingUtils.dismissLoading();
            }
        });
    }

    /**
     * Método para saber si se puede clickear el botón de "ya llegué"
     *
     * @return
     */
    private boolean canClickedArrived() {
        Calendar now = Calendar.getInstance();
        Calendar appointmentDate = Calendar.getInstance();
        appointmentDate.setTime(appointment.getDate());

        // Primero verificamos si la fecha del servicio es la misma fecha del dia
//        if (now.get(Calendar.DAY_OF_MONTH) != appointmentDate.get(Calendar.DAY_OF_MONTH)) {
//            AlertGlobal.showCustomError(activity, "Atención", "No puedes empezar el servicio antes de la fecha.");
//            return false;
//        }

        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onModelChange(BusEvents.ModelUpdated event) {
        if ("appointment".equals(event.model)) {
            setButtonStatus();
        }
    }

}
