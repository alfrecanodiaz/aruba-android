package py.com.aruba.clientes.data.models;

import android.app.ActivityManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import py.com.aruba.clientes.data.eventbus.BusEvents;
import py.com.aruba.clientes.data.eventbus.GlobalBus;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.utils.Print;

import static py.com.aruba.clientes.utils.UtilsParsing.getElement;

public class Device extends RealmObject {
    private static final String TAG = "Device";

    @PrimaryKey
    private Integer id;
    private Integer backend_id;
    private String extra_data;
    private String app_version;
    private String last_updated;
    private String last_login;
    private String push_token;
    private String version;
    private String phone_number;
    private boolean need_update;

    public Device() {
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

    public Integer getBackend_id() {
        return backend_id;
    }

    public void setBackend_id(Integer backend_id) {
        this.backend_id = backend_id;
    }

    public String getExtra_data() {
        return extra_data;
    }

    public void setExtra_data(String extra_data) {
        this.extra_data = extra_data;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }

    public String getLast_login() {
        return last_login;
    }

    public void setLast_login(String last_login) {
        this.last_login = last_login;
    }

    public boolean isNeed_update() {
        return need_update;
    }

    public void setNeed_update(boolean need_update) {
        this.need_update = need_update;
    }

    public String getPush_token() {
        return push_token;
    }

    public void setPush_token(String push_token) {
        this.push_token = push_token;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    /**
     * Método para crear o actualizar un Objeto de esta clase
     *
     * @param dataArray
     */
    public static void createOrUpdateArray(final JsonArray dataArray) {
        Realm realm = GlobalRealm.getDefault();
        try {
            realm.beginTransaction();
            for (JsonElement e : dataArray) {
                JsonObject data = e.getAsJsonObject();

                // Guardamos los datos del array
                Device m = getOrCreateObject(realm, data);
                realm.copyToRealmOrUpdate(setData(data, m));

                // Guardamos el telefono del usaurio en el caso de que esté
                if (!getElement(data, "phone_number", "").isEmpty()) {
                    User u = realm.where(User.class).findFirst();
                    u.setPhone(getElement(data, "phone_number", ""));
                    realm.copyToRealmOrUpdate(u);
                }

                GlobalBus.getBus().post(new BusEvents.ModelUpdated("device"));
            }
            realm.commitTransaction();
        } catch (Exception e) {
            Print.e(TAG, e);
        }
    }

    /**
     * Método para crear o retornar un objeto de la BD
     *
     * @param data
     * @return
     */
    private static Device getOrCreateObject(Realm realm, JsonObject data) {
        Device object = realm.where(Device.class).findFirst(); // Solo debemos tener un devices, por eso tomamos el first
        if (object == null) {
            object = new Device();
            object.setId(object.getId(realm));
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
    private static Device setData(JsonObject data, Device m) {
        m.setBackend_id(getElement(data, "id", 0));
        m.setPush_token(getElement(data, "push_token", ""));
        m.setPhone_number(getElement(data, "phone_number", ""));
        m.setExtra_data(getUserDeviceData());
        return m;
    }

    /**
     * Método para retornar los datos del dispositivo del usuario
     *
     * @return
     */
    private static String getUserDeviceData() {
        try {
            JsonObject data = new JsonObject();
            data.addProperty("device", android.os.Build.DEVICE);
            data.addProperty("model", android.os.Build.MODEL);
            data.addProperty("manufacturer", android.os.Build.MANUFACTURER);
            data.addProperty("version", android.os.Build.VERSION.RELEASE);
            data.addProperty("version_sdk", android.os.Build.VERSION.SDK_INT);
            data.addProperty("brand", Build.BRAND);


            // Obtenemos el espacio disponible
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            long bytesAvailable = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
            }
            long megAvailable = bytesAvailable / (1024 * 1024);

            // Obtenemos el tamaño de la memoria
//            ActivityManager actManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
//            actManager.getMemoryInfo(memInfo);
            long totalMemory = memInfo.totalMem / (1024 * 1024);

            data.addProperty("disk_space", megAvailable);
            data.addProperty("memory", totalMemory);


            return data.toString();
        } catch (Exception e) {
            return "ERROR: " + e.toString();
        }
    }

//    public static Device getDevice(Context context) {
//        Realm realm = GlobalRealm.getDefault();
//        realm.beginTransaction();
//
//        // Obtenemos el objeto
//        Device object = realm.where(Device.class).findFirst();
//        if (object == null) {
//            object = new Device();
//            object.setLocal_id(1);
//        }
//
//        object.setExtra_data(getUserDeviceData(context));
//        try {
//            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
//            String version = pInfo.versionName;
//            object.setApp_version(String.valueOf(pInfo.versionCode));
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        object.setLast_login(String.valueOf(MyApplication.session));
//
//        // Guardamos el objeto
//        realm.copyToRealmOrUpdate(object);
//        realm.commitTransaction();
//
//        return object;
//    }
}


