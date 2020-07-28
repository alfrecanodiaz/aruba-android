package py.com.aruba.profesionales.data.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Random;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.utils.Print;

import static py.com.aruba.profesionales.utils.UtilsParsing.getElement;

public class SubCategory extends RealmObject {
    private static final String TAG = "SubCategory";

    @PrimaryKey
    private Integer id;
    private String name;
    private String display_name;
    private String description;
    private String image_url;
    private boolean is_active;
    private boolean button_selected;
    private Category category;
    private Integer pos;
    private String client_type;
    private Boolean enabled; // if the professional offer the service or not

    // Variables de todas las tablas
    private String created_at;
    private String updated_at;
    private String deleted_at;

    /**
     * @param id
     * @param display_name
     * @param is_active
     * @param category
     * @param client_type
     */
    public SubCategory(Integer id, String display_name, boolean is_active, Category category, Integer client_type) {
        this.id = id;
        this.display_name = display_name;
        this.is_active = is_active;
        this.category = category;
    }

    public SubCategory() {
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public Integer getPos() {
        return pos;
    }

    public void setPos(Integer pos) {
        this.pos = pos;
    }

    public boolean isButton_selected() {
        return button_selected;
    }

    public void setButton_selected(boolean button_selected) {
        this.button_selected = button_selected;
    }

    public String getClient_type() {
        return client_type;
    }

    public void setClient_type(String client_type) {
        this.client_type = client_type;
    }

    /**
     * Método para crear o actualizar un Objeto de esta clase de forma sincrona
     *
     * @param realm
     * @param dataArray
     * @param category
     */
    public static void createOrUpdate(Realm realm, final JsonArray dataArray, Category category) {
        try {
            // Guardamos los datos del array
            for (JsonElement e : dataArray) {
                JsonObject data = e.getAsJsonObject();
                SubCategory model = getOrCreateObject(realm, data);
                realm.copyToRealmOrUpdate(setData(data, model, category));
            }
        } catch (Exception e) {
            Print.e(TAG, e);
        }
    }

    /**
     * Método para crear o retornar un objeto de la BD
     *
     * @param data
     * @return
     */
    private static SubCategory getOrCreateObject(Realm realm, JsonObject data) {
        int id = getElement(data, "id", 0);
        SubCategory object = realm.where(SubCategory.class).equalTo("id", id).findFirst();
        if (object == null) {
            object = new SubCategory();
            object.setId(id);// Una vez que se crea el objeto de forma plana, guardamos en realm y manejamos un objeto del tipo RealmObject
            object = realm.copyToRealmOrUpdate(object);
        }
        return object;
    }

    /**
     * Método para setear los datos a un Objeto de esta clase
     *
     * @param data
     * @param model
     * @param category
     * @return
     */
    private static SubCategory setData(JsonObject data, SubCategory model, Category category) {
        model.setName(getElement(data, "name", ""));
        model.setDisplay_name(getElement(data, "display_name", ""));
        model.setDescription(getElement(data, "description", ""));
        model.setImage_url(getElement(data, "imageUrl", ""));
        model.setIs_active(getElement(data, "is_active", true));

        // Obtenemos los tipos de clientes asociados a esta categoría
        JsonArray clientType = data.get("client_types").getAsJsonArray();
        if (clientType.size() > 0) {
            model.setClient_type(clientType.get(0).getAsJsonObject().get("id").getAsString());
        } else {
            // TODO: Temporal
            int[] array = {1, 2, 3};
            model.setClient_type(String.valueOf(array[new Random().nextInt(array.length)]));
        }

        // Seteamos la categoría padre
        model.setCategory(category);
        return model;
    }

    /**
     * Método para setear como enabled false todas las categorias
     */
    public static void setAllEnabledFalse() {
        Realm realm = GlobalRealm.getDefault();
        realm.beginTransaction();
        RealmResults<SubCategory> listCategories = realm.where(SubCategory.class).findAll();
        for(SubCategory c : listCategories){
            c.setEnabled(false);
        }
        realm.commitTransaction();
    }
}

