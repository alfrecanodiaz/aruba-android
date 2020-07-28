package py.com.aruba.profesionales.data.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
import py.com.aruba.profesionales.data.eventbus.BusEvents;
import py.com.aruba.profesionales.data.eventbus.GlobalBus;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.utils.Print;

import static py.com.aruba.profesionales.utils.UtilsParsing.getElement;

public class SubService extends RealmObject {
    private static final String TAG = "SubService";

    @PrimaryKey
    private Integer id;
    private String name;
    private String display_name;
    private String description;
    private String image_url;
    private int price;
    private int time;
    private boolean is_active;
    private boolean is_checked;
    private SubCategory subCategory;
    private boolean enabled; // if the professional offer the service or not

    // Variables de todas las tablas
    private String created_at;
    private String updated_at;
    private String deleted_at;

    public SubService() {
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public boolean getIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public SubCategory getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(SubCategory subCategory) {
        this.subCategory = subCategory;
    }

    public boolean isIs_checked() {
        return is_checked;
    }

    public void setIs_checked(boolean is_checked) {
        this.is_checked = is_checked;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isIs_active() {
        return is_active;
    }

    /**
     * @param id
     * @param name
     * @param subCategory
     */
    public SubService(Integer id, String name, SubCategory subCategory, int price) {
        this.id = id;
        this.display_name = name;
        this.subCategory = subCategory;
        this.price = price;
    }

    /**
     * Método para crear o actualizar un Objeto de esta clase de forma sincrona
     *
     * @param data
     */
    public static SubService createOrUpdate(final JsonObject data) {
        Realm realm = GlobalRealm.getDefault();
        SubService m = getOrCreateObject(realm, data);

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
    private static SubService getOrCreateObject(Realm realm, JsonObject data) {
        int id = getElement(data, "id", 0);
        SubService object = realm.where(SubService.class).equalTo("id", id).findFirst();
        if (object == null) {
            object = new SubService();
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
    private static SubService setData(JsonObject data, SubService m) {
        m.setName(getElement(data, "name", ""));
        m.setDisplay_name(getElement(data, "display_name", ""));
        m.setDescription(getElement(data, "description", ""));
        m.setImage_url(getElement(data, "image_url", ""));
        m.setPrice(getElement(data, "price", 0));
        m.setTime(getElement(data, "duration", 0));
        m.setIs_active(getElement(data, "is_active", true));
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
                SubService m = getOrCreateObject(bgRealm, data);
                m = setData(data, m);

                int sbID = data.get("categories").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsInt();
                SubCategory sb = bgRealm.where(SubCategory.class).equalTo("id", sbID).findFirst();
                m.setSubCategory(sb);

                bgRealm.copyToRealmOrUpdate(m);
            }
        }, () -> {
            Print.d(TAG, "Success");
            GlobalBus.getBus().post(new BusEvents.ModelUpdated("services"));
        }, error -> Print.e(TAG, error));
    }

    /**
     * Método para setear como enabled false todas las categorias
     */
    public static void setAllEnabledFalse() {
        Realm realm = GlobalRealm.getDefault();
        realm.beginTransaction();
        RealmResults<SubService> listServices = realm.where(SubService.class).findAll();
        for(SubService c : listServices){
            c.setEnabled(false);
        }
        realm.commitTransaction();
    }
}

