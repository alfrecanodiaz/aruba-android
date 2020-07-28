package py.com.aruba.clientes.data.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Random;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import py.com.aruba.clientes.utils.Print;

import static py.com.aruba.clientes.utils.UtilsParsing.getElement;

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
    private int order;
    private int client_type;

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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getClient_type() {
        return client_type;
    }

    public void setClient_type(int client_type) {
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
        model.setOrder(getElement(data, "order", 0));

        // Obtenemos los tipos de clientes asociados a esta categoría
        JsonArray clientType = data.get("client_types").getAsJsonArray();
        if (clientType.size() > 0) {
            model.setClient_type(clientType.get(0).getAsJsonObject().get("id").getAsInt());
        }

        // Seteamos la categoría padre
        model.setCategory(category);
        return model;
    }

}

