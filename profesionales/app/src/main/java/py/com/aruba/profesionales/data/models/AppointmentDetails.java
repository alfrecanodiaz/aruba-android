package py.com.aruba.profesionales.data.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

import static py.com.aruba.profesionales.utils.UtilsParsing.getElement;

public class AppointmentDetails extends RealmObject {
    private static final String TAG = "AppointmentDetails";

    @PrimaryKey
    private Integer id;
    private String title;
    private String description;
    private String image_url;
    private int sub_category_id;
    private int sub_service_id;
    private double price;
    private int time;
    private Appointment appointment;
    private SubService subService;
    private Category category;
    private SubCategory subCategory;

    public AppointmentDetails() {
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
            Number tempId = realm.where(AppointmentDetails.class).max("id");
            return (tempId == null) ? 1 : tempId.intValue() + 1;
        } else {
            return id;
        }
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

    public int getSub_category_id() {
        return sub_category_id;
    }

    public void setSub_category_id(int sub_category_id) {
        this.sub_category_id = sub_category_id;
    }

    public int getSub_service_id() {
        return sub_service_id;
    }

    public void setSub_service_id(int sub_service_id) {
        this.sub_service_id = sub_service_id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public SubService getSubService() {
        return subService;
    }

    public void setSubService(SubService subService) {
        this.subService = subService;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public SubCategory getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(SubCategory subCategory) {
        this.subCategory = subCategory;
    }

    /**
     * Método para crear o actualizar un Objeto de esta clase de forma sincrona
     *
     * @param dataArray
     * @param appointment
     * @param realm
     */
    static void createOrUpdate(final JsonArray dataArray, Appointment appointment, Realm realm) {
        for (JsonElement e : dataArray) {
            JsonObject data = e.getAsJsonObject();
            realm.copyToRealmOrUpdate(setData(realm, data, getOrCreateObject(realm, data, appointment), appointment));
        }
    }

    /**
     * Método para crear o retornar un objeto de la BD
     *
     * @param data
     * @param appointment
     * @return
     */
    private static AppointmentDetails getOrCreateObject(Realm realm, JsonObject data, Appointment appointment) {
        int id = getElement(data, "id", 0);


        AppointmentDetails object = realm.where(AppointmentDetails.class)
                .equalTo("sub_service_id", id)
                .equalTo("appointment.id", appointment.getId())
                .findFirst();

        if (object == null) {
            Number tempId = realm.where(AppointmentDetails.class).max("id");

            object = new AppointmentDetails();
            object.setId((tempId == null) ? 1 : tempId.intValue() + 1);// Una vez que se crea el objeto de forma plana, guardamos en realm y manejamos un objeto del tipo RealmObject
            object.setSub_service_id(id);
            object = realm.copyToRealmOrUpdate(object);
        }
        return object;
    }

    /**
     * Método para setear los datos a un Objeto de esta clase
     *
     * @param data
     * @param m
     * @param appointment
     * @return
     */
    private static AppointmentDetails setData(Realm realm, JsonObject data, AppointmentDetails m, Appointment appointment) {
        m.setTitle(getElement(data, "display_name", ""));
        m.setDescription(getElement(data, "description", ""));
        m.setTime(getElement(data, "duration", 0));
        m.setImage_url(getElement(data, "image_url", ""));
        m.setPrice(getElement(data, "price", 0.0));


        m.setAppointment(appointment);
        SubService ss = realm.where(SubService.class).equalTo("id", getElement(data, "id", 0)).findFirst();
        if (ss != null) {
            m.setSubService(ss);
            m.setSub_service_id(ss.getId());
            m.setSub_category_id(ss.getSubCategory().getId());
            m.setSubCategory(ss.getSubCategory());
            m.setCategory(ss.getSubCategory().getCategory());
        }
        return m;
    }

    /**
     * Método para retornar todos los detalles del appointment
     *
     * @param realm
     * @param appointment
     * @return
     */
    public static RealmResults<AppointmentDetails> getAllDetails(Realm realm, Appointment appointment) {
        RealmResults<AppointmentDetails> o = realm.where(AppointmentDetails.class)
                .equalTo("appointment.id", appointment.getId()).findAll();
        return o;
    }

    /**
     * Retornar un detalle especifico de una appointment
     *
     * @param realm
     * @param appointment
     * @param subservice
     * @return
     */
    public static AppointmentDetails getSpecificDetail(Realm realm, Appointment appointment, SubService subservice) {
        AppointmentDetails o = realm.where(AppointmentDetails.class)
                .equalTo("appointment.id", appointment.getId())
                .equalTo("sub_service_id", subservice.getId()).findFirst();

        return o;
    }
}

