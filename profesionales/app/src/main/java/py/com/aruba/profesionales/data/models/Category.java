package py.com.aruba.profesionales.data.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
import py.com.aruba.profesionales.data.eventbus.BusEvents;
import py.com.aruba.profesionales.data.eventbus.GlobalBus;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.utils.Constants;
import py.com.aruba.profesionales.utils.Print;

import static py.com.aruba.profesionales.utils.UtilsParsing.getElement;

public class Category extends RealmObject {
    private static final String TAG = "Category";

    @PrimaryKey
    private Integer id;
    private String name;
    private String display_name;
    private String description;
    private String inactive_text;
    private String image_url;
    private String color;
    private Boolean is_active;
    private String client_type;
    private boolean enabled; // if the professional offer the service or not

    public Category() {
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Boolean getIs_active() {
        return is_active;
    }

    public void setIs_active(Boolean is_active) {
        this.is_active = is_active;
    }

    public String getInactive_text() {
        return inactive_text;
    }

    public String getClient_type() {
        return client_type;
    }

    public void setClient_type(String client_type) {
        this.client_type = client_type;
    }

    public void setInactive_text(String inactive_text) {
        this.inactive_text = inactive_text;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Método para crear o actualizar un Objeto de esta clase de forma sincrona
     *
     * @param data
     */
    public static Category createOrUpdate(final JsonObject data) {
        Realm realm = GlobalRealm.getDefault();
        Category m = getOrCreateObject(realm, data);

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
    private static Category getOrCreateObject(Realm realm, JsonObject data) {
        int id = getElement(data, "id", 0);
        Category object = realm.where(Category.class).equalTo("id", id).findFirst();
        if (object == null) {
            object = new Category();
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
    private static Category setData(JsonObject data, Category m) {
        m.setName(getElement(data, "name", ""));
        m.setDisplay_name(getElement(data, "display_name", ""));
        m.setDescription(getElement(data, "description", ""));
        m.setImage_url(getElement(data, "imageUrl", ""));
        m.setColor(getElement(data, "color", "#333333"));
        m.setInactive_text(getElement(data, "inactive_text", "Próximamente"));
        m.setIs_active(getElement(data, "enabled", true));
        m.setImage_url(getElement(data, "image_url", Constants.DEFAULT_AVATAR));

        // Obtenemos los tipos de clientes asociados a esta categoría
        JsonArray clientTypesArray = data.get("client_types").getAsJsonArray();
        StringBuilder types = new StringBuilder();
        int i = 0;
        for (JsonElement e : clientTypesArray) {
            JsonObject elem = e.getAsJsonObject();
            if (i++ == clientTypesArray.size() - 1) {
                types.append(elem.get("id").getAsInt()).append(",");
            }
            types.append(elem.get("id").getAsInt());
        }

        m.setClient_type(types.toString());
        return m;
    }

    /**
     * Método para crear o actualizar un Objeto de esta clase
     *
     * @param dataArray
     */
    public static void createOrUpdateArrayBackground(final JsonArray dataArray) {
        GlobalRealm.getDefault().executeTransactionAsync(bgRealm -> {
            // Antes de insertar nuevos objetos eliminamos todos los guardados anteriormente
            bgRealm.delete(Category.class);

            // Iteramos por cada elemento dentro del array y obtenemos el objeto
            for (JsonElement e : dataArray) {
                JsonObject data = e.getAsJsonObject();

                // Guardamos los datos del array
                Category model = getOrCreateObject(bgRealm, data);
                bgRealm.copyToRealmOrUpdate(setData(data, model));

                // Guardamos los sub-servicios
                JsonArray subServicesArray = data.get("sub_categories").getAsJsonArray();
                SubCategory.createOrUpdate(bgRealm, subServicesArray, model);
            }
        }, () -> {
            Print.d(TAG, "Success");
            GlobalBus.getBus().post(new BusEvents.ModelUpdated("categories"));
            GlobalBus.getBus().post(new BusEvents.ModelUpdated("profile")); // Tambien el perfil porque ahi se visualizan las categorias del profesional
        }, error -> Print.e(TAG, error));
    }

    /**
     * Método para setear como enabled false todas las categorias
     */
    public static void setAllEnabledFalse() {
        Realm realm = GlobalRealm.getDefault();
        realm.beginTransaction();
        RealmResults<Category> listCategories = realm.where(Category.class).findAll();
        for(Category c : listCategories){
            c.setEnabled(false);
        }
        realm.commitTransaction();
    }

    /**
     * Retornamos un objeto realm del tipo RealmQuery
     * @param realm
     * @return
     */
    public static RealmQuery<Category> get(Realm realm) {
        return realm.where(Category.class);
    }
}

