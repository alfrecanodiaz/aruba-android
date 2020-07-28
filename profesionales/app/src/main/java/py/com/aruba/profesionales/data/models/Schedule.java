package py.com.aruba.profesionales.data.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import py.com.aruba.profesionales.data.eventbus.BusEvents;
import py.com.aruba.profesionales.data.eventbus.GlobalBus;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.utils.Print;

import static py.com.aruba.profesionales.utils.UtilsParsing.getElement;

public class Schedule extends RealmObject {
    private static final String TAG = "Schedule";

    @PrimaryKey
    private Integer id;
    private Integer week_day;
    private Integer hour_start;
    private Integer hour_end;
    private boolean enabled;
    private String start_hour_string;
    private String end_hour_string;

    // Variables de todas las tablas
    private String created_at;
    private String updated_at;
    private String deleted_at;

    public Schedule() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getWeek_day() {
        return week_day;
    }

    public void setWeek_day(Integer week_day) {
        this.week_day = week_day;
    }

    public Integer getHour_start() {
        return hour_start;
    }

    public void setHour_start(Integer hour_start) {
        this.hour_start = hour_start;
    }

    public Integer getHour_end() {
        return hour_end;
    }

    public void setHour_end(Integer hour_end) {
        this.hour_end = hour_end;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getStart_hour_string() {
        return start_hour_string;
    }

    public void setStart_hour_string(String start_hour_string) {
        this.start_hour_string = start_hour_string;
    }

    public String getEnd_hour_string() {
        return end_hour_string;
    }

    public void setEnd_hour_string(String end_hour_string) {
        this.end_hour_string = end_hour_string;
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

    /**
     * Método para crear o actualizar un Objeto de esta clase de forma sincrona
     *
     * @param data
     */
    public static Schedule createOrUpdate(final JsonObject data) {
        Realm realm = GlobalRealm.getDefault();
        Schedule m = getOrCreateObject(realm, data);

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
    private static Schedule getOrCreateObject(Realm realm, JsonObject data) {
        int id = getElement(data, "id", 0);
        Schedule object = realm.where(Schedule.class).equalTo("id", id).findFirst();
        if (object == null) {
            object = new Schedule();
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
    private static Schedule setData(JsonObject data, Schedule m) {
        m.setWeek_day(getElement(data, "week_day", 0));
        m.setHour_start(getElement(data, "hour_start", 0));
        m.setHour_end(getElement(data, "hour_end", 0));
        m.setEnabled(getElement(data, "enabled", true));
        m.setCreated_at(getElement(data, "created_at", ""));
        m.setUpdated_at(getElement(data, "updated_at", ""));

        m.setStart_hour_string(secondsToReadableHour(getElement(data, "hour_start", 0)));
        m.setEnd_hour_string(secondsToReadableHour(getElement(data, "hour_end", 0)));

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
                Schedule m = getOrCreateObject(bgRealm, data);
                m = setData(data, m);

                bgRealm.copyToRealmOrUpdate(m);
            }
        }, () -> {
            Print.d(TAG, "Success");
            GlobalBus.getBus().post(new BusEvents.ModelUpdated("schedules"));
        }, error -> Print.e(TAG, error));
    }

    private static String secondsToReadableHour(int seconds){
        TimeZone tz = TimeZone.getTimeZone("UTC");
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        df.setTimeZone(tz);
        String time = df.format(new Date(seconds*1000L));

        return time;
    }
}

