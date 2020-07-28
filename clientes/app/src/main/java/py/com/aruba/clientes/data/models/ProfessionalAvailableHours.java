package py.com.aruba.clientes.data.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import py.com.aruba.clientes.utils.Constants;
import py.com.aruba.clientes.utils.Print;
import py.com.aruba.clientes.utils.TimeUtils;

public class ProfessionalAvailableHours extends RealmObject {
    private static final String TAG = "ProfessionalHours";

    @PrimaryKey
    public Integer id;
    public Integer professional_id;
    public Integer hour_start;
    public Integer hour_end;


    public ProfessionalAvailableHours() {
    }


    /**
     * Guardamos todos los horarios disponibles de los profesionales
     *
     * @param realm
     * @param array
     * @param professional_id
     * @return
     */
    public static void create(Realm realm, JsonArray array, int professional_id) {
        realm.beginTransaction();
        for (JsonElement e : array) {
            JsonObject data = e.getAsJsonObject();

            ProfessionalAvailableHours row = new ProfessionalAvailableHours();
            row.id = (int) UUID.randomUUID().getMostSignificantBits();
            row.hour_start = data.get("hour_start").getAsInt();
            row.hour_end = data.get("hour_end").getAsInt();
            row.professional_id = professional_id;

            realm.copyToRealm(row);
        }
        realm.commitTransaction();
    }

    /**
     * Eliminamos todos los horarios guardados de los professionals
     *
     * @param realm
     */
    public static void delete(Realm realm) {
        realm.beginTransaction();
        realm.where(ProfessionalAvailableHours.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    /**
     * Retornamos los horarios disponibles del profesional
     *
     * @param realm
     * @param professional_id
     * @return
     */
    public static List<JsonObject>  get_hours(Realm realm, int professional_id, int service_duration) {
        List<JsonObject> hours = new ArrayList();
        int i = 1;

        // Obtenemos la disponibilidad de los profesionales
        List<ProfessionalAvailableHours> list = realm.where(ProfessionalAvailableHours.class).equalTo("professional_id", professional_id).findAll();

        // Iteramos por cada uno de los slots disponibles del profesinal
        for (ProfessionalAvailableHours pa : list) {
            int current_hour = pa.hour_start;

            while (current_hour <= pa.hour_end) {
                // Rompemos el while solamente en el final del horario
                if (current_hour + service_duration > pa.hour_end) break;

                // Solamente agregamos al array si la duración del servicio es menor al to_hour disponible
                if (current_hour + service_duration <= pa.hour_end) {
                    JsonObject data = new JsonObject();
                    data.addProperty("id", i);
                    data.addProperty("hour_start", current_hour);
                    data.addProperty("hour_end", current_hour + service_duration);
                    data.addProperty("readable_hour", TimeUtils.secondsToHour(current_hour));
                    hours.add(data);
                    i++;
                }
                // Aumentamos cada XX tiempo
                current_hour += Constants.TIME_BETWEEN_HOURS;
            }
        }
        return hours;
    }

}


