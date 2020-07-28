package py.com.aruba.profesionales.ui.profile.workzone;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import py.com.aruba.profesionales.R;
import py.com.aruba.profesionales.data.eventbus.BusEvents;
import py.com.aruba.profesionales.data.eventbus.GlobalBus;
import py.com.aruba.profesionales.data.helpers.ResponseData;
import py.com.aruba.profesionales.data.helpers.RestAdapter;
import py.com.aruba.profesionales.data.helpers.request.RequestResponseDataJsonObject;
import py.com.aruba.profesionales.data.models.Zone;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.data.service.ProfileInterface;
import py.com.aruba.profesionales.data.service.ZoneInterface;
import py.com.aruba.profesionales.utils.alerts.AlertGlobal;
import py.com.aruba.profesionales.utils.dialogs.CustomDialog;
import py.com.aruba.profesionales.utils.loading.UtilsLoading;
import retrofit2.Call;
import tyrantgit.explosionfield.ExplosionField;

public class MapZoneActivity extends AppCompatActivity implements OnMapReadyCallback {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rlContentMarker) RelativeLayout rlContentMarker;
    @BindView(R.id.rlAgregarPin) RelativeLayout rlAgregarPin;
    @BindView(R.id.rlEliminarPin) RelativeLayout rlEliminarPint;
    @BindView(R.id.rlGuardarPin) RelativeLayout rlGuardarPunto;

    private GoogleMap mMap;
    private Context context;
    private Location userLocation;
    private List<CustomMarker> listMarkers;
    private Polygon polygon;
    private ExplosionField explosionField;
    private Marker selectedMarker;
    private ProfileInterface profileInterface;
    private Zone zone;

    //    private SellerProfile sellerProfile;
    private Realm realm;
    private Integer ZONE_ID;
    private boolean IS_NEW;

    private int butonStatus = 0; // 0: agregar pin 1: eliminar pin 2: guardar pin 3: guardar zona
    private UtilsLoading loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_zone);
        ButterKnife.bind(this);
        context = this;
        realm = GlobalRealm.getDefault();
        IS_NEW = getIntent().getBooleanExtra("is_new", true);
        ZONE_ID = getIntent().getIntExtra("zone_id", 0);

        setupUI();

        explosionField = ExplosionField.attach2Window(this);
        zone = realm.where(Zone.class).findFirst(); // Obtenemos el primero porque en esta etapa solo se trabaja con una zona de cobertura
        initializeData();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        checkButonStatus(0);
    }

    /**
     * Método para inicializar el diseño de la interfaz
     */
    private void setupUI() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_zone, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_guardar) {
            if (listMarkers.size() > 2) {
                saveZones();
            } else {
                AlertGlobal.showMessage(context, "¡Atención", "Para poder guardar la zona de entrega, es necesario que haya al menos 3 puntos");
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @OnClick(R.id.rlBackButton)
    public void closeActivity(View view) {
        onBackPressed();
    }

    /**
     * Métodos Onclicks de este activity
     *
     * @param view
     */
    @OnClick({R.id.rlAgregarPin, R.id.rlEliminarPin, R.id.rlGuardarPin})
    public void onButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.rlAgregarPin:
                checkButonStatus(2);
                break;
            case R.id.rlGuardarPin:
                LatLng center = mMap.getCameraPosition().target;
                addMarker(center);
                drawWaypoints();
                checkButonStatus(0);
                break;
            case R.id.rlEliminarPin:
                deleteMarker();
                drawWaypoints();
                checkButonStatus(0);
                break;
        }
    }

    /**
     * Método para manejar la visibilidad de los botones
     */
    private void checkButonStatus(int status) {
        // 0: agregar pin 1: eliminar pin 2: guardar pin
        butonStatus = status;

        // Reiniciamos la visibilidad
        rlAgregarPin.setVisibility(View.INVISIBLE);
        rlContentMarker.setVisibility(View.INVISIBLE);
        rlEliminarPint.setVisibility(View.INVISIBLE);
        rlGuardarPunto.setVisibility(View.INVISIBLE);

        switch (butonStatus) {
            case 0:
                rlAgregarPin.setVisibility(View.VISIBLE);
                break;
            case 1:
                rlEliminarPint.setVisibility(View.VISIBLE);
                break;
            case 2:
                rlContentMarker.setVisibility(View.VISIBLE);
                rlGuardarPunto.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * Método para guardar los nuvos waypoints
     */
    private void saveZones() {
        loading = new UtilsLoading(this);
        loading.showLoading();
        JsonArray listOfPoints = new JsonArray();

        // Iteramos por toda la lista para poder generar la zona de entrega
        for (CustomMarker cm : listMarkers) {
            JsonObject object = new JsonObject();
            object.addProperty("lat", cm.position.latitude);
            object.addProperty("lng", cm.position.longitude);

            listOfPoints.add(object);
        }

        // Volvemos a agregar el primer punto para que se cierre el ciclo
        JsonObject object = new JsonObject();
        object.addProperty("lat", listMarkers.get(0).position.latitude);
        object.addProperty("lng", listMarkers.get(0).position.longitude);
        listOfPoints.add(object);

        // Guardamos los puntos en el array de coordinates
        JsonObject zoneObject = new JsonObject();
        zoneObject.add("coordinates", listOfPoints);


        sendToBackend(zoneObject);
    }

    /**
     * Método para enviar al backend el waypoint generado
     * @param zoneObject
     */
    private void sendToBackend(JsonObject zoneObject) {
        Call<ResponseData<JsonElement>> call;

        // Si es nuevo creamos una nueva zona, de lo contrario actualizamos
        if(IS_NEW){
            call = RestAdapter.getClient(context).create(ZoneInterface.class).create(zoneObject);
        }else{
            zoneObject.addProperty("id", zone.getId());
            call = RestAdapter.getClient(context).create(ZoneInterface.class).update(zoneObject);
        }


        call.enqueue(new RequestResponseDataJsonObject("zone") {
            @Override
            public void successful(JsonObject object) {
                JsonArray dataArray = new JsonArray();
                dataArray.add(object);
                Zone.createOrUpdateArrayBackground(dataArray);
                GlobalBus.getBus().post(new BusEvents.ModelUpdated("profile"));
                AlertGlobal.showCustomSuccess(MapZoneActivity.this, "Atención", "¡Guardado Correctamente!", new CustomDialog.ButtonsListener() {
                    @Override
                    public void onDialogPositiveClick(DialogFragment dialog) {
                        MapZoneActivity.this.finish();
                    }

                    @Override
                    public void onDialogNegativeClick(DialogFragment dialog) {

                    }
                });
                loading.dismissLoading();
            }

            @Override
            public void successful(String msg) {
                AlertGlobal.showCustomSuccess(MapZoneActivity.this, "Atención", msg);
                loading.dismissLoading();
            }

            @Override
            public void error(String msg) {
                AlertGlobal.showCustomError(MapZoneActivity.this, "Atención", msg);
                loading.dismissLoading();
            }
        });
    }

    /**
     * Método para inicializar las variables
     */
    private void initializeData() {
        listMarkers = new ArrayList<>();
    }

    /**
     * Método para agregar los marcadores al mapa
     *
     * @param position
     */
    private void addMarker(LatLng position) {

        // Agregamos marcadores
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(position)
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        marker.setTitle(String.valueOf(listMarkers.size()));
        marker.setTag(listMarkers.size());
        listMarkers.add(new CustomMarker(listMarkers.size(), marker, position));

        // Escuchamos el drag del marker
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                updateMarkerList(marker);
                drawWaypoints();
                checkButonStatus(0);
            }
        });

        // Escuchamos el onclick del marker
        mMap.setOnMarkerClickListener(marker1 -> {
            selectedMarker = marker1;
            checkButonStatus(1);
            return false;
        });
    }

    /**
     * Método para actualizar la lista de marcadores y/o posiciones
     *
     * @param marker
     */
    private void updateMarkerList(Marker marker) {
        for (CustomMarker cm : listMarkers) {
            if (cm.id == (int) marker.getTag()) {
                cm.marker = marker;
                cm.position = marker.getPosition();
            }
        }
    }

    /**
     * Método para eliminar un marker del listado y del mapa
     */
    private void deleteMarker() {
        boolean removed = false;
        for (int i = 0; i < listMarkers.size(); i++) {
            if (removed) return;
            CustomMarker cm = listMarkers.get(i);
            if (cm.id == (int) selectedMarker.getTag()) {
                cm.marker.remove();
                listMarkers.remove(i);
                removed = true;
            }
        }
    }

    /**
     * Método para ubicar los waypoints en el mapa
     */
    private void drawWaypoints() {
        LatLng[] lista = new LatLng[listMarkers.size()];
        // iteramos para obtener las posiciones
        for (int i = 0; i < lista.length; i++) {
            lista[i] = listMarkers.get(i).position;
        }

        if (polygon != null) polygon.remove();

        polygon = mMap.addPolygon(
                new PolygonOptions()
                        .add(lista)
                        .strokeColor(Color.rgb(0, 50, 100))
                        .strokeWidth(5)
                        .fillColor(Color.argb(30, 50, 0, 255))
        );
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        // Agregamos el marcador del usuario
        addProfileLocation();

        if (!IS_NEW) {
            // Cargamos la zona si es que esta guardada
            if (zone != null) {
                LatLng[] lista = Zone.getCoordinates(zone.getZone());
                for (LatLng location : lista) addMarker(location);
                drawWaypoints();
            }
        }
    }

    /**
     * Método para agregar el marcador en la posición donde se encuentra el local del usuario y setear la ubicación en la variable userLocation
     */
    private void addProfileLocation() {
        userLocation = new Location("");
        userLocation.setLatitude(-25.2845847);
        userLocation.setLongitude(-57.5692807);
        LatLng loc = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());

        // Animamos el mapa
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 500, null);
    }

    /**
     * Clase para guardar los marcadores que se van agregando al mapa
     */
    class CustomMarker {
        int id;
        Marker marker;
        LatLng position;

        CustomMarker(int id, Marker marker, LatLng position) {
            this.id = id;
            this.marker = marker;
            this.position = position;
        }
    }
}
