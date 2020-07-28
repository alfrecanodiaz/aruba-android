package py.com.aruba.clientes.data.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.utils.Print;

import static py.com.aruba.clientes.utils.UtilsParsing.getElement;

public class SubServicePoints extends RealmObject {
    private static final String TAG = "SubService";

    @PrimaryKey
    private Integer id;
    private int point;
    private SubService subService;

    // Variables de todas las tablas
    private String created_at;
    private String updated_at;
    private String deleted_at;

    public SubServicePoints() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public SubService getSubService() {
        return subService;
    }

    public void setSubService(SubService subService) {
        this.subService = subService;
    }

    /**
     * @param id
     * @param point
     * @param subService
     */
    public SubServicePoints(Integer id, int point, SubService subService) {
        this.id = id;
        this.point = point;
        this.subService = subService;
    }

    /**
     * Método para crear o actualizar un Objeto de esta clase de forma sincrona
     *
     * @param data
     */
    public static SubServicePoints createOrUpdate(final JsonObject data) {
        Realm realm = GlobalRealm.getDefault();
        SubServicePoints m = getOrCreateObject(realm, data);

        try {
            realm.beginTransaction();
            // Guardamos los datos del array
            m = setData(data, m);
            realm.copyToRealmOrUpdate(m);
            realm.commitTransaction();
        } catch (Exception e) {
            Print.e(TAG, e);
            return null;
        }

        return m;
    }

    /**
     * Método para crear o retornar un objeto de la BD
     *
     * @param data
     * @return
     */
    private static SubServicePoints getOrCreateObject(Realm realm, JsonObject data) {
        int id = getElement(data, "id", 0);
        SubServicePoints object = realm.where(SubServicePoints.class).equalTo("id", id).findFirst();
        if (object == null) {
            object = new SubServicePoints();
            object.setId(id);// Una vez que se crea el objeto de forma plana, guardamos en realm y manejamos un objeto del tipo RealmObject
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
    private static SubServicePoints setData(JsonObject data, SubServicePoints m) {
//        m.setName(getElement(data, "name", ""));
//        m.setDisplay_name(getElement(data, "display_name", ""));
//        m.setDescription(getElement(data, "description", ""));
//        m.setImage_url(getElement(data, "image_url", ""));
//        m.setIs_active(getElement(data, "is_active", true));
        return m;
    }

    /**
     * Método para crear o actualizar un Objeto de esta clase
     *
     * @param dataArray
     */
    public static void createOrUpdateArrayBackground(final JsonArray dataArray) {
        GlobalRealm.getDefault().executeTransactionAsync(bgRealm -> {
            // Iteramos por cada elemento dentro del array y obtenemos el objeto
            for (JsonElement e : dataArray) {
                JsonObject data = e.getAsJsonObject();

                // Guardamos los datos del array
                SubServicePoints m = getOrCreateObject(bgRealm, data);
                m = setData(data, m);
                bgRealm.copyToRealmOrUpdate(m);
            }
        }, () -> Print.d(TAG, "Success"), error -> Print.e(TAG, error));
    }

}

