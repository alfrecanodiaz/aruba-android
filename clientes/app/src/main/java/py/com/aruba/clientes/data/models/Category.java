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
import py.com.aruba.clientes.utils.Constants;
import py.com.aruba.clientes.utils.Print;

import static py.com.aruba.clientes.utils.UtilsParsing.getElement;

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
    private int order;
    private Boolean is_active;
    private String client_type;

    public Category() {
    }

    public Category(int i, String s, String s1, String s2, boolean is_active) {
        this.id = i;
        this.display_name = s;
        this.inactive_text = s1;
        this.color = s2;
        this.is_active = is_active;
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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
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
        m.setOrder(getElement(data, "order", 0));
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
                types.append(elem.get("id").getAsInt());
            }else{
                types.append(elem.get("id").getAsInt()).append(",");
            }
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
        Realm realm = GlobalRealm.getDefault();
        realm.executeTransactionAsync(bgRealm -> {

            // Primero eliminamos todas las sub-categorias y los sub-servicios porque no se manejan deleted-at
            bgRealm.where(SubService.class).findAll().deleteAllFromRealm();
            bgRealm.where(SubCategory.class).findAll().deleteAllFromRealm();
            bgRealm.where(Category.class).findAll().deleteAllFromRealm();

            // Iteramos por cada elemento dentro del array y obtenemos el objeto
            for (JsonElement e : dataArray) {
                JsonObject data = e.getAsJsonObject();

                // Guardamos los datos del array
                Category model = getOrCreateObject(bgRealm, data);
                bgRealm.copyToRealmOrUpdate(setData(data, model));

                // Guardamos las sub-categorias
                JsonArray sub_categories = data.get("sub_categories").getAsJsonArray();
                SubCategory.createOrUpdate(bgRealm, sub_categories, model);
            }
        }, () -> {
            Print.d(TAG, "Success");
            GlobalBus.getBus().post(new BusEvents.ModelUpdated("categories"));
        }, error -> Print.e(TAG, error));
    }

}

