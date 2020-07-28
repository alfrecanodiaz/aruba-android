package py.com.aruba.profesionales.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UtilsParsing {

    /**
     * Obtenemos y devolvemos el elemento de un Json del tipo String y controlamos los null pointers
     *
     * @param object
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getElement(JsonObject object, String key, String defaultValue) {
        if (object.has(key)) {
            JsonElement e = object.get(key);
            if (e == null || e.isJsonNull()) {
                return defaultValue;
            } else {
                return e.getAsString();
            }
        } else {
            return defaultValue;
        }
    }

    /**
     * Obtenemos y devolvemos el elemento de un Json del tipo int y controlamos los null pointers
     *
     * @param object
     * @param key
     * @param defaultValue
     * @return
     */
    public static int getElement(JsonObject object, String key, int defaultValue) {
        if (object.has(key)) {
            JsonElement e = object.get(key);
            if (e == null || e.isJsonNull()) {
                return defaultValue;
            } else {
                return e.getAsInt();
            }
        } else {
            return defaultValue;
        }
    }

    /**
     * Obtenemos y devolvemos el elemento de un Json del tipo int y controlamos los null pointers
     *
     * @param object
     * @param key
     * @param defaultValue
     * @return
     */
    public static Double getElement(JsonObject object, String key, Double defaultValue) {
        if (object.has(key)) {
            JsonElement e = object.get(key);
            if (e == null || e.isJsonNull()) {
                return defaultValue;
            } else {
                return e.getAsDouble();
            }
        } else {
            return defaultValue;
        }
    }

    /**
     * Obtenemos y devolvemos el elemento de un Json del tipo boolean y controlamos los null pointers
     *
     * @param object
     * @param key
     * @param defaultValue
     * @return
     */
    public static boolean getElement(JsonObject object, String key, boolean defaultValue) {
        if (object.has(key)) {
            JsonElement e = object.get(key);
            if (e == null || e.isJsonNull()) {
                return defaultValue;
            } else {
                return e.getAsBoolean();
            }
        } else {
            return defaultValue;
        }
    }

    /**
     * Obtenemos y devolvemos el elemento de un Json del tipo boolean y controlamos los null pointers
     *
     * @param object
     * @param key
     * @return
     */
    public static Date getElementDate(JsonObject object, String key, String format) {
        if (object.has(key)) {

            try {
                JsonElement e = object.get(key);
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH); // here set the pattern as you date in string was containing like date/month/year
                if (e == null || e.isJsonNull()) {
                    return null;
                } else {
                    Date d = sdf.parse(e.getAsString());
                    return d;
                }

            } catch (ParseException ex) {
                // handle parsing exception if date string was different from the pattern applying into the SimpleDateFormat contructor
                return null;
            }
        } else {
            return null;
        }
    }
}
