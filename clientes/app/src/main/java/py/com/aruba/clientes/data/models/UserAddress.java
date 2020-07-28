package py.com.aruba.clientes.data.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
import py.com.aruba.clientes.data.eventbus.BusEvents;
import py.com.aruba.clientes.data.eventbus.GlobalBus;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.utils.Print;

import static py.com.aruba.clientes.utils.UtilsParsing.getElement;

public class UserAddress extends RealmObject {
    private static final String TAG = "UserAddress";

    @PrimaryKey
    private Integer id;
    private Integer backend_id;
    private String street1;
    private String street2;
    private String name;
    private String references;
    private String number;
    private String lat;
    private String lng;
    private boolean is_default;
    private boolean is_active;

    // Variables de todas las tablas
    private String created_at;
    private String updated_at;
    private String deleted_at;

    public UserAddress() {
    }

    public Integer getId() {
        return id;
    }

    /**
     * Autoincrement local ID
     *
     * @param realm
     * @return
     */
    public Integer getId(Realm realm) {
        if (id == null) {
            // Generate autoincrement: https://stackoverflow.com/questions/40174920/how-to-set-primary-key-auto-increment-in-realm-android/40175572
            Number tempId = realm.where(Appointment.class).max("id");
            return (tempId == null) ? 1 : tempId.intValue() + 1;
        } else {
            return id;
        }
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBackend_id() {
        return backend_id;
    }

    public void setBackend_id(Integer backend_id) {
        this.backend_id = backend_id;
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReferences() {
        return references;
    }

    public void setReferences(String references) {
        this.references = references;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public boolean isIs_default() {
        return is_default;
    }

    public void setIs_default(boolean is_default) {
        this.is_default = is_default;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public String getLocation() {
        return String.format("%s,%s", this.lat, this.lng);
    }

    /**
     * Obtenemos la dirección compuesta
     *
     * @return
     */
    public String getFullAddress() {
        return String.format("%s c/ %s", this.street1, this.street2);
    }

    /**
     * Método para crear o actualizar un Objeto de esta clase
     *
     * @param dataArray
     */
    public static void createOrUpdateArrayBackground(final JsonArray dataArray) {
        GlobalRealm.getDefault().executeTransactionAsync(realm -> {
                    // Iteramos por cada elemento dentro del array
                    for (JsonElement e : dataArray) {
                        JsonObject data = e.getAsJsonObject();

                        // Guardamos los datos del array
                        UserAddress m = getOrCreateObject(realm, data);
                        m = setData(data, m);
                        realm.copyToRealmOrUpdate(m);
                    }
                },
                () -> GlobalBus.getBus().post(new BusEvents.ModelUpdated("address")),
                error -> Print.e(TAG, error));
    }

    /**
     * Método para crear o retornar un objeto de la BD
     *
     * @param data
     * @return
     */
    private static UserAddress getOrCreateObject(Realm realm, JsonObject data) {
        int backend_id = getElement(data, "id", 0);
        UserAddress object = realm.where(UserAddress.class).equalTo("backend_id", backend_id).findFirst();
        if (object == null) {
            object = new UserAddress();
            object.setId(backend_id);
            object.setBackend_id(backend_id);
            // Una vez que se crea el objeto de forma plana, guardamos en realm y manejamos un objeto del tipo RealmObject
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
    private static UserAddress setData(JsonObject data, UserAddress m) {
        // campos normales
        m.setStreet1(getElement(data, "street1", ""));
        m.setStreet2(getElement(data, "street2", ""));
        m.setName(getElement(data, "name", ""));
        m.setNumber(getElement(data, "number", ""));
        m.setReferences(getElement(data, "references", ""));
        m.setLat(getElement(data, "lat", ""));
        m.setLng(getElement(data, "lng", ""));
        m.setIs_default(getElement(data, "is_default", false));
        m.setIs_active(getElement(data, "is_active", false));

        // TimeStamps
        m.setCreated_at(getElement(data, "created_at", ""));
        m.setUpdated_at(getElement(data, "updated_at", ""));
        m.setDeleted_at(getElement(data, "deleted_at", ""));
        return m;
    }

    /**
     * Método para devolver la dirección principal
     *
     * @return
     */
    public static UserAddress getDefaultAddress() {
        Realm realm = GlobalRealm.getDefault();
        UserAddress object = realm.where(UserAddress.class).equalTo("is_default", true).findFirst();
        return object;
    }

    /**
     * Método para devolver la dirección principal
     *
     * @return
     */
    public static String getDefaultAddressString() {
        Realm realm = GlobalRealm.getDefault();
        UserAddress object = realm.where(UserAddress.class).equalTo("is_default", true).findFirst();
        if (object != null) {
            return String.format("%s c/ %s", object.getStreet1(), object.getStreet2());
        } else {
            return "";
        }
    }

    /**
     * Retornamos el ID de la dirección que está por default
     *
     * @return
     */
    public static Integer getDefaultAddressID() {
        Realm realm = GlobalRealm.getDefault();
        UserAddress object = realm.where(UserAddress.class).equalTo("is_default", true).findFirst();
        if (object != null) {
            return object.getBackend_id();
        } else {
            return 0;
        }
    }

    /**
     * Seteamos todas las direcciones como is_default=false
     */
    public static void resetAll(Realm realm) {
        RealmResults<UserAddress> listAddresses = realm.where(UserAddress.class).findAll();

        realm.beginTransaction();
        for (UserAddress a : listAddresses) {
            a.setIs_default(false);
        }
        realm.commitTransaction();
    }

    /**
     * Seteamos todas las direcciones como is_default=false
     */
    public static void deleteAll() {
        GlobalRealm.getDefault().beginTransaction();
        GlobalRealm.getDefault().delete(UserAddress.class);
        GlobalRealm.getDefault().commitTransaction();
    }

    /**
     * Seteamos la dirección como default
     */
    public static void setDefault(int id) {
        Realm realm = GlobalRealm.getDefault();
        UserAddress address = realm.where(UserAddress.class).equalTo("id", id).findFirst();
        // Seteamos como default la dirección
        if (address != null) {
            realm.beginTransaction();
            address.setIs_default(true);
            realm.commitTransaction();
        }

        GlobalBus.getBus().post(new BusEvents.ModelUpdated("address"));
    }


    public static RealmResults<UserAddress> getAll(Realm realm) {
        return realm.where(UserAddress.class).findAll();
    }

}

