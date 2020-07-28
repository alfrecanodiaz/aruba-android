package py.com.aruba.profesionales.data.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.annotations.PrimaryKey;
import py.com.aruba.profesionales.data.eventbus.BusEvents;
import py.com.aruba.profesionales.data.eventbus.GlobalBus;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.utils.Constants;
import py.com.aruba.profesionales.utils.Print;

import static py.com.aruba.profesionales.utils.UtilsParsing.getElement;

public class Review extends RealmObject {
    private static final String TAG = "Review";

    @PrimaryKey
    private Integer id;
    private Integer client_id;
    private Integer professional_id;
    private String username;
    private String client_avatar;
    private String comment;
    private Integer calification;

    // Variables de todas las tablas
    private String created_at;
    private String updated_at;
    private String deleted_at;

    public Review() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClient_id() {
        return client_id;
    }

    public void setClient_id(Integer client_id) {
        this.client_id = client_id;
    }

    public Integer getProfessional_id() {
        return professional_id;
    }

    public void setProfessional_id(Integer professional_id) {
        this.professional_id = professional_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getCalification() {
        return calification;
    }

    public void setCalification(Integer calification) {
        this.calification = calification;
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

    public String getClient_avatar() {
        return client_avatar;
    }

    public void setClient_avatar(String client_avatar) {
        this.client_avatar = client_avatar;
    }

    /**
     * Método para crear o actualizar un Objeto de esta clase de forma sincrona
     *
     * @param data
     */
    public static Review createOrUpdate(final JsonObject data) {
        Realm realm = GlobalRealm.getDefault();
        Review m = getOrCreateObject(realm, data);

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
    private static Review getOrCreateObject(Realm realm, JsonObject data) {
        int id = getElement(data, "id", 0);
        Review object = realm.where(Review.class).equalTo("id", id).findFirst();
        if (object == null) {
            object = new Review();
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
    private static Review setData(JsonObject data, Review m) {
        m.setProfessional_id(getElement(data, "user_id", 0));
        m.setClient_id(getElement(data, "reviewer_id", 0));
        m.setCalification(getElement(data, "rating_number", 0));
        m.setComment(getElement(data, "text", ""));
        m.setCreated_at(getElement(data, "created_at", ""));
        m.setUpdated_at(getElement(data, "updated_at", ""));

        // TODO: Ver si esta validación es necesaria, porque sin reviewer no se puede mostrar datos duh!
        if(!data.get("reviewer").isJsonNull()){
            JsonObject reviewer = data.get("reviewer").getAsJsonObject();
            String user = String.format("%s %s", getElement(reviewer, "first_name", ""), getElement(reviewer, "last_name", ""));
            m.setUsername(user);
            m.setClient_avatar(getElement(reviewer, "avatar_url", Constants.DEFAULT_AVATAR));
        }

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
                Review m = getOrCreateObject(bgRealm, data);
                m = setData(data, m);

                bgRealm.copyToRealmOrUpdate(m);
            }
        }, () -> {
            Print.d(TAG, "Success");
            GlobalBus.getBus().post(new BusEvents.ModelUpdated("reviews"));
        }, error -> Print.e(TAG, error));
    }

    /**
     * Retornamos un objeto realm del tipo RealmQuery
     * @param realm
     * @return
     */
    public static RealmQuery<Review> get(Realm realm) {
        return realm.where(Review.class);
    }
}

