package py.com.aruba.profesionales.data.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.Normalizer;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import py.com.aruba.profesionales.data.eventbus.BusEvents;
import py.com.aruba.profesionales.data.eventbus.GlobalBus;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.utils.Constants;
import py.com.aruba.profesionales.utils.Print;

import static py.com.aruba.profesionales.utils.UtilsParsing.getElement;

public class Professional extends RealmObject {
    private static final String TAG = "Professional";

    @PrimaryKey
    private Integer id;
    private String first_name = "";
    private String last_name = "";
    private String email;
    private int favorites;
    private String categories;
    // Datos
    private int likes_count;
    private int services_count;
    private int reviews_count;
    private String average_reviews;
    private String avatar;

    // Campo para las busquedas sin acentos, fucking spanish
    private String search_data;


    public Professional() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getFavorites() {
        return favorites;
    }

    public void setFavorites(int favorites) {
        this.favorites = favorites;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public int getLikes_count() {
        return likes_count;
    }

    public void setLikes_count(int likes_count) {
        this.likes_count = likes_count;
    }

    public int getServices_count() {
        return services_count;
    }

    public void setServices_count(int services_count) {
        this.services_count = services_count;
    }

    public int getReviews_count() {
        return reviews_count;
    }

    public void setReviews_count(int reviews_count) {
        this.reviews_count = reviews_count;
    }

    public String getAverage_reviews() {
        return average_reviews;
    }

    public void setAverage_reviews(String average_reviews) {
        this.average_reviews = average_reviews;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSearch_data() {
        return search_data;
    }

    public void setSearch_data(String search_data) {
        this.search_data = search_data;
    }

    /**
     * Método para crear o retornar un objeto de la BD
     *
     * @param data
     * @return
     */
    private static Professional getOrCreateObject(Realm realm, JsonObject data) {
        int id = getElement(data, "id", 0);
        Professional object = realm.where(Professional.class).equalTo("id", id).findFirst();
        if (object == null) {
            object = new Professional();
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
    private static Professional setData(JsonObject data, Professional m) {
        m.setFirst_name(getElement(data, "first_name", ""));
        m.setLast_name(getElement(data, "last_name", ""));
        m.setEmail(getElement(data, "email", ""));
        m.setAvatar(getElement(data, "avatar_url", Constants.DEFAULT_AVATAR));
        m.setLikes_count(getElement(data, "likes", 0));
        m.setServices_count(getElement(data, "services_count", 0));
        String average = getElement(data, "average_reviews", "1.0");
        m.setAverage_reviews(String.valueOf(Math.round(Float.parseFloat(average)))); // Acá pasamos al string, al float y después al int
        JsonArray categories = data.get("categories").getAsJsonArray();
        if (categories.size() > 0) {
            String categories_id = "";
            for (JsonElement e : categories) {
                JsonObject cat = e.getAsJsonObject();
                // Agregamos el ID de la categoría
                categories_id = categories_id + String.format("%s;", getElement(cat, "id", ""));
            }
            m.setCategories(categories_id);
        }


        // Campo para las búsquedas
        m.setSearch_data(String.format("%s %s %s ", noAccents(m.getFirst_name()), noAccents(m.getLast_name()), noAccents(m.getEmail())));

        return m;
    }

    private static String noAccents(String str){
        String st = Normalizer.normalize(str, Normalizer.Form.NFD);
        return Normalizer.normalize(st, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    private static int getProfessionalReviews(int id, Realm realm) {
        return (int) realm.where(Review.class).equalTo("professional_id", id).count();
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
                Professional m = getOrCreateObject(bgRealm, data);
                m = setData(data, m);
                m.setReviews_count(getProfessionalReviews(getElement(data, "id", 0), bgRealm));

                bgRealm.copyToRealmOrUpdate(m);
            }
        }, () -> {
            Print.d(TAG, "Success");
            GlobalBus.getBus().post(new BusEvents.ModelUpdated("professional"));
        }, error -> Print.e(TAG, error));
    }
}


