package py.com.aruba.profesionales.data.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.annotations.PrimaryKey;
import py.com.aruba.profesionales.data.eventbus.BusEvents;
import py.com.aruba.profesionales.data.eventbus.GlobalBus;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.utils.Print;

import static py.com.aruba.profesionales.utils.UtilsParsing.getElement;

public class Zone extends RealmObject {
    private static final String TAG = "Zone";

    @PrimaryKey
    private Integer id;
    private String title;
    private String zone;
    private boolean enabled;

    public Zone() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Método para crear o actualizar un Objeto de esta clase
     * @param dataArray
     */
    public static void createOrUpdateArrayBackground(final JsonArray dataArray) {
        GlobalRealm.getDefault().executeTransactionAsync(bgRealm -> {
            // Iteramos por cada elemento dentro del array y obtenemos el objeto
            for (JsonElement e : dataArray) {
                JsonObject data = e.getAsJsonObject();

                // Guardamos los datos del array
                Zone m = getOrCreateObject(bgRealm, data);
                m = setData(data, m);

                bgRealm.copyToRealmOrUpdate(m);
            }
        }, () -> {
            Print.d(TAG, "Success");
            GlobalBus.getBus().post(new BusEvents.ModelUpdated("zone"));
        }, error -> Print.e(TAG, error));
    }

    /**
     * Método para crear o retornar un objeto de la BD
     *
     * @param data
     * @return
     */
    private static Zone getOrCreateObject(Realm realm, JsonObject data) {
        int id = getElement(data, "id", 0);
        Zone object = realm.where(Zone.class).equalTo("id", id).findFirst();
        if (object == null) {
            object = new Zone();
            object.setId(id);
            object = realm.copyToRealmOrUpdate(object);
        }
        return object;
    }

    /**
     * Método para setear los datos a un Objeto de esta clase
     *
     * @param data
     * @param m
     * @return
     */
    private static Zone setData(JsonObject data, Zone m) {
        m.setTitle(getElement(data, "title", ""));
        m.setEnabled(getElement(data, "enabled", true));
        JsonArray points = data.get("polygon").getAsJsonObject().get("coordinates").getAsJsonArray();
        m.setZone(arrayToString(points));
        return m;
    }

    /**
     * Metodo para convertir objetos waypoints en string
     *
     * @param lista Objeto waypoint
     * @return Retorna string
     */
    public static String arrayToString(JsonArray lista) {
        // TODO: Ver para que sea mas rápido el parsing y la consulta
        StringBuilder valor = new StringBuilder();
        int i = 1;

        for(JsonElement firstItem : lista){
            for (JsonElement val : firstItem.getAsJsonArray()) {
                JsonArray e = val.getAsJsonArray();
                // Si es el ultimo omitimos el punto y coma
                if (i++ == firstItem.getAsJsonArray().size()) {
                    valor.append(e.get(1).getAsString()).append(",").append(e.get(0).getAsString());
                } else {
                    valor.append(e.get(1).getAsString()).append(",").append(e.get(0).getAsString()).append(";");
                }
            }
        }

        return valor.toString();
    }

    /**
     * Metodo para convertir string en objetos waypoints
     *
     * @param list
     * @return Retorna objetos waypoints
     */
    public static LatLng[] getCoordinates(String list) {
        String[] listSplit = list.split(";");
        ArrayList<LatLng> points = new ArrayList<LatLng>();

        for (String s : listSplit) {
            LatLng obj = new LatLng(Double.parseDouble(s.split(",")[0]), Double.parseDouble(s.split(",")[1]));
            points.add(obj);
        }

        return points.toArray(new LatLng[points.size()]);
    }

    /**
     * Retornamos un objeto realm del tipo RealmQuery
     * @param realm
     * @return
     */
    public static RealmQuery<Zone> get(Realm realm) {
        return realm.where(Zone.class);
    }
}
