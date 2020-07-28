package py.com.aruba.clientes.data.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import py.com.aruba.clientes.data.eventbus.BusEvents;
import py.com.aruba.clientes.data.eventbus.GlobalBus;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.utils.Print;

import static py.com.aruba.clientes.utils.UtilsParsing.getElement;

public class Promotions extends RealmObject {
    private static final String TAG = "Promotions";

    @PrimaryKey
    private Integer id;
    private String title;
    private String description;
    private String image_url;
    private boolean active;

    public Promotions() {
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

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Promotions(Integer id, String title, String image_url) {
        this.id = id;
        this.title = title;
        this.image_url = image_url;
    }

    /**
     * Método para crear o actualizar un Objeto de esta clase de forma sincrona
     *
     * @param data
     */
    public static Promotions createOrUpdate(final JsonObject data) {
        Realm realm = GlobalRealm.getDefault();
        Promotions m = getOrCreateObject(realm, data);

        try {
            realm.where(Promotions.class).findAll().deleteAllFromRealm();
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
    private static Promotions getOrCreateObject(Realm realm, JsonObject data) {
        int id = getElement(data, "id", 0);
        Promotions object = realm.where(Promotions.class).equalTo("id", id).findFirst();
        if (object == null) {
            object = new Promotions();
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
    private static Promotions setData(JsonObject data, Promotions m) {
        m.setImage_url(getElement(data, "image_url", ""));
        m.setTitle(getElement(data, "name", ""));
        m.setDescription(getElement(data, "description", ""));
        m.setActive(getElement(data, "active", false));
        return m;
    }

    /**
     * Método para crear o actualizar un Objeto de esta clase
     *
     * @param dataArray
     */
    public static void createOrUpdateArrayBackground(final JsonArray dataArray) {
        GlobalRealm.getDefault().executeTransactionAsync(bgRealm -> {
            bgRealm.where(Promotions.class).findAll().deleteAllFromRealm();

            // Iteramos por cada elemento dentro del array y obtenemos el objeto
            for (JsonElement e : dataArray) {
                JsonObject data = e.getAsJsonObject();

                // Guardamos los datos del array
                Promotions m = getOrCreateObject(bgRealm, data);
                m = setData(data, m);

                bgRealm.copyToRealmOrUpdate(m);
            }
        }, () -> {
            Print.d(TAG, "Success");
            GlobalBus.getBus().post(new BusEvents.ModelUpdated("promotions"));
        }, error -> Print.e(TAG, error));
    }
}

