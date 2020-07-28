package py.com.aruba.profesionales.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {

    /**
     * Método para obtener el objeto shared preferences
     *
     * @param context
     * @return
     */
    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    /**
     * Método para obtener el editor de sharedPreferences
     *
     * @param context
     * @return
     */
    private static SharedPreferences.Editor getEditor(Context context) {
        return getSharedPreferences(context).edit();
    }

    // Set string
    public static void setValue(Context context, String prefsKey, String prefsValue) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(prefsKey, prefsValue);
        editor.apply();
    }

    // Set int
    public static void setValue(Context context, String prefsKey, int prefsValue) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putInt(prefsKey, prefsValue);
        editor.apply();
    }

    // Set boolean
    public static void setValue(Context context, String prefsKey, boolean prefsValue) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(prefsKey, prefsValue);
        editor.apply();
    }

    // Get String
    public static String getString(Context context, String prefsKey) {
        String value = getSharedPreferences(context).getString(prefsKey, "");
        return value;
    }

    // Get int
    public static int getInt(Context context, String prefsKey) {
        int value = getSharedPreferences(context).getInt(prefsKey, 0);
        return value;
    }

    // Get boolean
    public static Boolean getBoolean(Context context, String prefsKey) {
        Boolean value = getSharedPreferences(context).getBoolean(prefsKey, false);
        return value;
    }

    // Get boolean true default
    public static Boolean getBooleanTrue(Context context, String prefsKey) {
        Boolean value = getSharedPreferences(context).getBoolean(prefsKey, true);
        return value;
    }

    // Limpiamos
    public static void clearSharedPreference(Context context) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.clear();
        editor.apply();
    }

    // Removemos una key
    public static void removeValue(Context context, String key) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.remove(key);
        editor.apply();
    }
}
