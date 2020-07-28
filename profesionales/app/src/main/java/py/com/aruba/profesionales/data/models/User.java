package py.com.aruba.profesionales.data.models;

import android.content.Context;

import com.google.gson.JsonObject;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import py.com.aruba.profesionales.data.eventbus.BusEvents;
import py.com.aruba.profesionales.data.eventbus.GlobalBus;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.utils.Constants;
import py.com.aruba.profesionales.utils.Print;
import py.com.aruba.profesionales.utils.SharedPreferencesUtils;

import static py.com.aruba.profesionales.utils.UtilsParsing.getElement;

public class User extends RealmObject {
    private static final String TAG = "User";

    @PrimaryKey
    private Integer id;

    private String first_name = "";
    private String last_name = "";
    private String email;
    private String token;
    private String avatar = Constants.DEFAULT_AVATAR;
    private String birthday;
    private Integer score;
    private String phone;
    private String document;
    private String address_default = "";
    private int address_id_default;

    public User() {
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getAddress_default() {
        return address_default;
    }

    public void setAddress_default(String address_default) {
        this.address_default = address_default;
    }

    public int getAddress_id_default() {
        return address_id_default;
    }

    public void setAddress_id_default(int address_id_default) {
        this.address_id_default = address_id_default;
    }

    /**
     * Método para devolver el nombre completo
     *
     * @return
     */
    public String getFullName() {
        return String.format("%s %s", this.getFirst_name(), this.getLast_name());
    }

    /**
     * Método para crear o actualizar un Objeto de esta clase de forma sincrona
     * @param object
     * @param context
     * @return
     */
    public static void createOrUpdate(final JsonObject object, Context context) {
        Realm realm = GlobalRealm.getDefault();

        try {
            // Empezamos la transacción de realm
            realm.beginTransaction();
            JsonObject userDataObject = object.get("user").getAsJsonObject();
            String token = object.get("access_token").getAsString();

            // Eliminamos el usuario local ya que se va a crear o actualizar
            realm.delete(User.class);

            // Datos del usuario
            User u = new User();
            u.setId(getElement(userDataObject,"id", 0));
            u.setFirst_name(getElement(userDataObject,"first_name", ""));
            u.setLast_name(getElement(userDataObject,"last_name", ""));
            u.setEmail(getElement(userDataObject,"email", ""));
            u.setAvatar(getElement(userDataObject,"avatar_url", ""));
            u.setBirthday(getElement(userDataObject,"birthdate", ""));
            u.setDocument(getElement(userDataObject,"document", ""));
            u.setToken(token);

            // Guardamos en realm
            realm.copyToRealmOrUpdate(u);
            realm.commitTransaction();

            // Guardamos la bandera en shared preferences
            SharedPreferencesUtils.setValue(context, Constants.TOKEN, token);
            SharedPreferencesUtils.setValue(context, Constants.LOGGED, true);

            GlobalBus.getBus().post(new BusEvents.ModelUpdated("profile"));
        } catch (Exception e) {
            Print.e(TAG, e);
        }
    }

    /**
     * Método para retornar el unico usuario guardado en la BD
     *
     * @return
     */
    public static User getUser() {
        return GlobalRealm.getDefault().where(User.class).findFirst();
    }


}

