package py.com.aruba.clientes.ui.profile.mapsLocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.models.UserAddress;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.ui.main.MainActivity;
import py.com.aruba.clientes.utils.Print;
import py.com.aruba.clientes.utils.Utils;
import py.com.aruba.clientes.utils.dialogs.CustomDialog;

public class RegisterMapLocationActivity extends AppCompatActivity implements OnMapReadyCallback, EasyPermissions.PermissionCallbacks {
    private static final int ACCESS_FINE_LOCATION = 123;
    public static boolean CLOSE_ACTIVITY;

    @BindView(R.id.btnGuardar)
    Button btnGuardar;
    @BindView(R.id.rlContentMarker)
    RelativeLayout rlContentMarker;
    @BindView(R.id.etSearch)
    EditText etSearch;

    private GoogleMap mMap;
    private Activity context;
    private Realm realm;

    private String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private Location userLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationManager locationManager = null;
    private boolean is_edit_mode;
    private boolean go_to_main;
    private int address_id;
    private UserAddress addressObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_register_map_location);
        ButterKnife.bind(this);
        context = this;
        realm = GlobalRealm.getDefault();

        is_edit_mode = getIntent().getBooleanExtra("is_edit_mode", false);
        go_to_main = getIntent().getBooleanExtra("go_to_main", true);
        address_id = getIntent().getIntExtra("address_id", 0);


        // Obtenemos el objeto dirección si estamos en modo edición
        if (is_edit_mode)
            addressObject = realm.where(UserAddress.class).equalTo("backend_id", address_id).findFirst();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setupUI();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (CLOSE_ACTIVITY) {
            CLOSE_ACTIVITY = false;
            if (is_edit_mode) {
                finish();
            } else {
                if (go_to_main) {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();
                } else {
                    finish();
                }
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setPadding(0, 170, 0, 0);

        // Si estamos en modo edición, agregamos el marcador de la dirección del usuario.
        if (is_edit_mode) {
            String location = addressObject.getLocation();
            double latitude = Double.parseDouble(location.split(",")[0]);
            double longitude = Double.parseDouble(location.split(",")[1]);
            userLocation = new Location("");
            userLocation.setLatitude(latitude);
            userLocation.setLongitude(longitude);
            moveCameraToLocation(userLocation.getLatitude(), userLocation.getLongitude());
        } else {
            // Le ubicamos al usuario en un lugar X, hasta que se ubique por GPS
            userLocation = new Location("");
            userLocation.setLatitude(-25.2968361);
            userLocation.setLongitude(-57.6681296);
            moveCameraToLocation(userLocation.getLatitude(), userLocation.getLongitude());

            initLocation();
        }
    }


    /**
     * Método para inicializar el diseño de la interfaz
     */
    private void setupUI() {
        if (is_edit_mode) {
            btnGuardar.setText("Actualizar Dirección");
        }

        // Escuchamos el action done del textedit
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || event.getAction() == KeyEvent.ACTION_DOWN
                    || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                geocoderAddress();
            }

            return false;
        });
    }


    /**
     * Método para obtener las coordenadas a través del Geocoder de google
     */
    private void geocoderAddress() {
        String searchString = etSearch.getText().toString() + ",PY";
        Geocoder geocoder = new Geocoder(RegisterMapLocationActivity.this);

        try {
            List<Address> list = geocoder.getFromLocationName(searchString, 1);
            if (list.size() > 0) {
                Address address = list.get(0);
                moveCameraToLocation(address.getLatitude(), address.getLongitude());
            } else {
                Toast.makeText(this, "No se encontró la ubicación solicitada", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Print.e("mapa", "geoLocate: IOException: " + e.getMessage());
        }
    }


    /**
     * Método para mover la camara del mapa a la ubicación establecida
     */
    private void moveCameraToLocation(double latitude, double longitude) {
        LatLng loc = new LatLng(latitude, longitude);
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(loc, 13);
        mMap.animateCamera(location);
    }


    /**
     * Método para solicitar encender el GPS
     */
    private void checkGPS() {
        if (EasyPermissions.hasPermissions(this, perms)) {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // Le mostramos el popup para que encienda el GPS
                GPSDialog gps = new GPSDialog();
                gps.setListener(new CustomDialog.ButtonsListener() {
                    @Override
                    public void onDialogPositiveClick(DialogFragment dialog) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        dialog.dismiss();
                    }

                    @Override
                    public void onDialogNegativeClick(DialogFragment dialog) {
                        dialog.dismiss();
                    }
                });
                gps.show(getSupportFragmentManager(), "GPSDialog");
            }
        }

    }


    /**
     * Inicializamos los servicios de ubicación
     */
    @SuppressLint("MissingPermission")
    private void initLocation() {
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, getString(R.string.location_permission), ACCESS_FINE_LOCATION, perms);
        } else {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(() -> {
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    checkGPS();
                }
                return false;
            });

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            getLocation();
        }
    }


    @SuppressLint("MissingPermission")
    private void getLocation() {
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, getString(R.string.location_permission), ACCESS_FINE_LOCATION, perms);
        } else {
            checkGPS();
            if (mFusedLocationClient != null) {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(context, location -> {
                    if (location != null) {
                        userLocation = location;
                        moveCameraToLocation(location.getLatitude(), location.getLongitude());
                    }
                });
            } else {
                Toast.makeText(context, "No pudimos obtener tu ubicación", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @OnClick({R.id.btnGuardar, R.id.rlBackButton, R.id.ic_search})
    public void onButtonClick(View view) {
        Utils.preventTwoClick(view);
        switch (view.getId()) {
            case R.id.rlBackButton:
                onBackPressed();
                break;
            case R.id.btnGuardar:
                // Seteamos el centro del mapa en las variables
                final LatLng center = mMap.getCameraPosition().target;
                userLocation.setLatitude(center.latitude);
                userLocation.setLongitude(center.longitude);

                // Iniciamos el activity para registrar el formulario de direcciones
                Intent intent = new Intent(context, RegisterAddressFormActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("lat", String.valueOf(center.latitude));
                intent.putExtra("lng", String.valueOf(center.longitude));
                intent.putExtra("is_edit_mode", is_edit_mode);
                intent.putExtra("address_id", address_id);
                startActivity(intent);
                break;
            case R.id.ic_search:
                RegisterMapLocationActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                geocoderAddress();
                break;
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mMap.clear();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Volvemos a llamar a los métodos de location cuando vuelve de la pantalla de settings
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            initLocation();
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
            initLocation();
        }
    }


    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }

}