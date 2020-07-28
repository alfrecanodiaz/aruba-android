package py.com.aruba.profesionales.data.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Date;

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
import static py.com.aruba.profesionales.utils.UtilsParsing.getElementDate;

public class Appointment extends RealmObject {
    private static final String TAG = "Appointment";

    @PrimaryKey
    private Integer id;
    private Integer backend_id;

    private String category;
    private int category_id;

    private String lat;
    private String lng;
    private String address;
    private int address_id;

    private String client;
    private String client_avatar;
    private String client_phone;
    private int client_type;

    private Date date; // Fecha legible para el usuario
    private String hour_start_pretty; // Hora legible para el usuario
    private String hour_end_pretty; // Hora legible para el usuario
    private int from_hour;
    private int to_hour;
    private int duration;

    // Datos para el countdown
    private boolean in_progress;
    private int start_at = 0;

    private String professional;
    private Integer professional_id;

    private double amount;
    private double professional_earnings;
    private double client_amount;

    private int status;
    private String payment_method;

    // Variables de todas las tablas
    private String created_at;
    private String updated_at;
    private String deleted_at;


    public Appointment() {
    }

    public Integer getId() {
        return id;
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
            Number tempId = realm.where(Appointment.class).max("id");
            return (tempId == null) ? 1 : tempId.intValue() + 1;
        } else {
            return id;
        }
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isIn_progress() {
        return in_progress;
    }

    public void setIn_progress(boolean in_progress) {
        this.in_progress = in_progress;
    }

    public int getStart_at() {
        return start_at;
    }

    public void setStart_at(int start_at) {
        this.start_at = start_at;
    }

    public Integer getBackend_id() {
        return backend_id;
    }

    public void setBackend_id(Integer backend_id) {
        this.backend_id = backend_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAddress_id() {
        return address_id;
    }

    public void setAddress_id(int address_id) {
        this.address_id = address_id;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public int getClient_type() {
        return client_type;
    }

    public void setClient_type(int client_type) {
        this.client_type = client_type;
    }

    public String getHour_start_pretty() {
        return hour_start_pretty;
    }

    public void setHour_start_pretty(String hour_start_pretty) {
        this.hour_start_pretty = hour_start_pretty;
    }

    public String getHour_end_pretty() {
        return hour_end_pretty;
    }

    public void setHour_end_pretty(String hour_end_pretty) {
        this.hour_end_pretty = hour_end_pretty;
    }

    public int getFrom_hour() {
        return from_hour;
    }

    public void setFrom_hour(int from_hour) {
        this.from_hour = from_hour;
    }

    public int getTo_hour() {
        return to_hour;
    }

    public void setTo_hour(int to_hour) {
        this.to_hour = to_hour;
    }

    public String getProfessional() {
        return professional;
    }

    public void setProfessional(String professional) {
        this.professional = professional;
    }

    public Integer getProfessional_id() {
        return professional_id;
    }

    public void setProfessional_id(Integer professional_id) {
        this.professional_id = professional_id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getProfessional_earnings() {
        return professional_earnings;
    }

    public void setProfessional_earnings(double professional_earnings) {
        this.professional_earnings = professional_earnings;
    }

    public double getClient_amount() {
        return client_amount;
    }

    public void setClient_amount(double client_amount) {
        this.client_amount = client_amount;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getClient_avatar() {
        return client_avatar;
    }

    public void setClient_avatar(String client_avatar) {
        this.client_avatar = client_avatar;
    }

    public String getClient_phone() {
        return client_phone;
    }

    public void setClient_phone(String client_phone) {
        this.client_phone = client_phone;
    }

    /**
     * Método para crear o actualizar un Objeto de esta clase de forma sincrona
     *
     * @param data
     */
    public static Appointment createOrUpdate(final JsonObject data) {
        Realm realm = GlobalRealm.getDefault();
        Appointment m = getOrCreateObject(realm, data);

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
     * Método para crear o actualizar un Objeto de esta clase
     *
     * @param dataArray
     */
    public static void createOrUpdateArrayBackground(final JsonArray dataArray) {
        GlobalRealm.getDefault().executeTransactionAsync(realm -> {
                    // Iteramos por cada elemento dentro del array
                    for (JsonElement e : dataArray) {
                        JsonObject data = e.getAsJsonObject();

                        // Guardamos los datos del array
                        Appointment m = getOrCreateObject(realm, data);
                        m = setData(data, m);
                        realm.copyToRealmOrUpdate(m);

                        // Detalles del appointment
                        JsonArray services = data.get("services").getAsJsonArray();
                        AppointmentDetails.createOrUpdate(services, m, realm);
                    }
                },
                () -> GlobalBus.getBus().post(new BusEvents.ModelUpdated("appointment")),
                error -> Print.e(TAG, error));
    }

    /**
     * Método para crear o retornar un objeto de la BD
     *
     * @param data
     * @return
     */
    private static Appointment getOrCreateObject(Realm realm, JsonObject data) {
        int backend_id = getElement(data, "id", 0);
        Appointment object = realm.where(Appointment.class).equalTo("backend_id", backend_id).findFirst();
        if (object == null) {
            object = new Appointment();
            object.setId(object.getId(realm));
            object.setBackend_id(backend_id);
            // Una vez que se crea el objeto de forma plana, guardamos en realm y manejamos un objeto del tipo RealmObject
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
    private static Appointment setData(JsonObject data, Appointment m) {
        m.setProfessional_id(getElement(data, "user_appointer_id", 0));
        m.setClient(getElement(data, "client_name", ""));
        m.setBackend_id(getElement(data, "id", 0));
        m.setAddress_id(getElement(data, "address_id", 0));
        m.setAddress(getElement(data, "full_address", ""));
        m.setLat(getElement(data, "lat", ""));
        m.setLng(getElement(data, "lng", ""));
        m.setAmount(getElement(data, "price", 0));
        m.setProfessional_earnings(getElement(data, "professional_earnings", 0));
        m.setDuration(getElement(data, "duration", 0));
        m.setFrom_hour(getElement(data, "hour_start", 0));
        m.setTo_hour(getElement(data, "hour_end", 0));
        m.setDate(getElementDate(data, "date", "yyyy-MM-dd"));
        m.setPayment_method(getElement(data, "payment_method", ""));
        m.setHour_start_pretty(getElement(data, "hour_start_pretty", ""));
        m.setHour_end_pretty(getElement(data, "hour_end_pretty", ""));
        m.setClient_phone(getElement(data, "client_phone_number", ""));
        m.setCreated_at(getElement(data, "created_at", ""));
        m.setUpdated_at(getElement(data, "updated_at", ""));

        JsonObject current_state = data.get("current_state").getAsJsonObject();
        JsonObject transaction = data.get("transaction").getAsJsonObject();
        JsonObject transactionable = transaction.get("transactionable").getAsJsonObject();
        JsonObject client = data.get("client").getAsJsonObject();

        m.setStatus(current_state.get("id").getAsInt());
        m.setClient_amount(getElement(transactionable, "client_amount", 0.0));
        m.setClient_avatar(getElement(client, "avatar_url", ""));
        return m;
    }

    /**
     * Retornamos un objeto realm del tipo RealmQuery
     * @param realm
     * @return
     */
    public static RealmQuery<Appointment> get(Realm realm) {
        return realm.where(Appointment.class);
    }

    public static String getStatusString(int status) {
        switch (status) {
            case Constants.APPOINTMENT_STATUS.CREATED:
                return "Pendiente";
            case Constants.APPOINTMENT_STATUS.ARRIVED:
                return "En puerta";
            case Constants.APPOINTMENT_STATUS.IN_PROGRESS:
                return "En progreso";
            case Constants.APPOINTMENT_STATUS.COMPLETED:
                return "Completado";
            case Constants.APPOINTMENT_STATUS.CANCELED:
                return "Cancelado";
            default:
                return "";
        }
    }

    public static String getDurationString(int duration) {
        int hours = duration / 3600;
        int minutes = (duration % 3600) / 60;
        String durationStr = (hours > 0) ? String.format("%sh y %smin", hours, minutes) : String.format("%s minutos", minutes);
        return durationStr;
    }

    /**
     * Obtenemos el Appointment a través del ID
     * @param appointment_id
     * @return
     */
    public static Appointment getByBackendID(int appointment_id) {
        return GlobalRealm.getDefault().where(Appointment.class).equalTo("backend_id", appointment_id).findFirst();
    }
}

