package py.com.aruba.profesionales.data.helpers.request;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.realm.Realm;
import io.realm.RealmResults;
import py.com.aruba.profesionales.data.eventbus.BusEvents;
import py.com.aruba.profesionales.data.eventbus.GlobalBus;
import py.com.aruba.profesionales.data.helpers.ResponseData;
import py.com.aruba.profesionales.data.helpers.RestAdapter;
import py.com.aruba.profesionales.data.models.Appointment;
import py.com.aruba.profesionales.data.models.Category;
import py.com.aruba.profesionales.data.models.Device;
import py.com.aruba.profesionales.data.models.Professional;
import py.com.aruba.profesionales.data.models.Review;
import py.com.aruba.profesionales.data.models.Schedule;
import py.com.aruba.profesionales.data.models.SubCategory;
import py.com.aruba.profesionales.data.models.SubService;
import py.com.aruba.profesionales.data.models.User;
import py.com.aruba.profesionales.data.models.Zone;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.data.service.AppointmentInterface;
import py.com.aruba.profesionales.data.service.CategoryInterface;
import py.com.aruba.profesionales.data.service.ProfileInterface;
import py.com.aruba.profesionales.data.service.RankingInterface;
import py.com.aruba.profesionales.data.service.ReviewsInterface;
import py.com.aruba.profesionales.data.service.ZoneInterface;
import py.com.aruba.profesionales.utils.Constants;
import py.com.aruba.profesionales.utils.SharedPreferencesUtils;
import py.com.aruba.profesionales.utils.alerts.AlertGlobal;
import retrofit2.Call;

import static py.com.aruba.profesionales.utils.UtilsParsing.getElement;

public class ManageGeneralRequest {

    private final Context context;

    public ManageGeneralRequest(Context context) {
        this.context = context;
    }

    public void downloadAll() {
        downloadCategories();
        downloadSubServices(1);
        downloadMyZones();
        downloadSchedule();
        downloadReviews(1);
        downloadDevice();
        downloadRanking(1);
    }

    /**
     * Download user data
     */
    public void me() {
        RestAdapter.getClient(context).create(ProfileInterface.class).me().enqueue(new RequestResponseDataJsonObject("profile") {
            @Override
            public void successful(JsonObject object) {
                Realm realm = GlobalRealm.getDefault();
                User u = realm.where(User.class).findFirst();

                // Empezamos la transacción de realm
                realm.beginTransaction();
                u.setFirst_name(getElement(object, "first_name", ""));
                u.setLast_name(getElement(object, "last_name", ""));
                u.setEmail(getElement(object, "email", ""));
                u.setBirthday(getElement(object, "birthdate", ""));
                u.setDocument(getElement(object, "document", ""));
                u.setAvatar(getElement(object, "avatar_url", ""));

                // Guardamos en realm
                realm.copyToRealmOrUpdate(u);
                realm.commitTransaction();


                GlobalBus.getBus().post(new BusEvents.ModelUpdated("profile"));
            }

            @Override
            public void successful(String msg) {

            }

            @Override
            public void error(String msg) {

            }
        });
    }

    /**
     * Metodo para setear en el backend cuales son las categorias activas del usaurio
     */
    public static void setActiveCategories(Context context, Realm realm) {
        // Obtenemos todas las categorias
        RealmResults<Category> listCategoreis = realm.where(Category.class).equalTo("enabled", true).findAll();

        // Guardamos el array de las categorias activas
        JsonArray categoriesArray = new JsonArray();
        for (Category e : listCategoreis) categoriesArray.add(e.getId());

        // Generamos el object que se enviara al backend
        JsonObject categoriesObject = new JsonObject();
        categoriesObject.add("service_category_ids", categoriesArray);

        // Enviamos al backend
        RestAdapter.getClient(context).create(CategoryInterface.class).store_categories(categoriesObject).enqueue(new RequestResponseDataJsonObject("categories") {
            @Override
            public void successful(JsonObject object) {

            }

            @Override
            public void successful(String msg) {

            }

            @Override
            public void error(String msg) {

            }
        });
    }

    /**
     * Metodo para setear en el backend cuales son los servicios del usaurio
     */
    public static void setActiveServices(Context context, Realm realm) {
        // Obtenemos todas las categorias
        RealmResults<SubService> listServices = realm.where(SubService.class).equalTo("enabled", true).findAll();

        // Guardamos el array de las categorias activas
        JsonArray servicesArray = new JsonArray();
        for (SubService e : listServices) servicesArray.add(e.getId());

        // Generamos el object que se enviara al backend
        JsonObject servicesObject = new JsonObject();
        servicesObject.add("service_ids", servicesArray);

        // Enviamos al backend
        RestAdapter.getClient(context).create(CategoryInterface.class).store_services(servicesObject).enqueue(new RequestResponseDataJsonObject("services") {
            @Override
            public void successful(JsonObject object) {

            }

            @Override
            public void successful(String msg) {

            }

            @Override
            public void error(String msg) {

            }
        });
    }

    /**
     * Descargamos todas las categorias disponibles
     */
    public void downloadCategories() {
        CategoryInterface restInterface = RestAdapter.getClient(context).create(CategoryInterface.class);
        restInterface.get_categories().enqueue(new RequestResponseDataJsonArray("categories") {
            @Override
            public void successful(JsonArray dataArray) {
                Category.createOrUpdateArrayBackground(dataArray);

                // Descargamos los datos que dependen de las categorias
                downloadMyCategories();
                downloadMyServices();
                downloadAppointment(1);
            }

            @Override
            public void successful(String msg) {

            }

            @Override
            public void error(String msg) {

            }
        });
    }

    /**
     * Descargamos los servicios
     *
     * @param page
     */
    private void downloadSubServices(int page) {
        // Instanciamos el adapter del cliente rest
        CategoryInterface restInterface = RestAdapter.getClient(context).create(CategoryInterface.class);

        // Realizamos la petición al backend
        restInterface.get_sub_services(page).enqueue(new RequestResponseDataPaginator(page, "service") {
            @Override
            public void successful(JsonArray dataArray, int page, boolean hasNextPage) {
                SubService.createOrUpdateArrayBackground(dataArray);
                if (hasNextPage) downloadSubServices(page);
            }

            @Override
            public void error(String msg) {
            }
        });
    }

    /**
     * metodo para descargar las categorias asociadas al profesional
     */
    private void downloadMyCategories() {
        CategoryInterface restInterface = RestAdapter.getClient(context).create(CategoryInterface.class);
        restInterface.get_my_categories().enqueue(new RequestResponseDataJsonArray("categories") {
            @Override
            public void successful(JsonArray dataArray) {
                Realm realm = GlobalRealm.getDefault();
                Category.setAllEnabledFalse();
                SubCategory.setAllEnabledFalse();
                for (JsonElement e : dataArray) {
                    JsonObject object = e.getAsJsonObject();
                    JsonArray sub_category = object.get("sub_categories").getAsJsonArray();
                    realm.beginTransaction();

                    // Si tenemos subcatgorias en el objeto, entonces es una categoria principal. De lo contrario es una subcatgoria
                    if (sub_category.size() > 0) {
                        Category c = realm.where(Category.class).equalTo("id", object.get("id").getAsInt()).findFirst();
                        if (c != null) c.setEnabled(true);
                    } else {
                        SubCategory sc = realm.where(SubCategory.class).equalTo("id", object.get("id").getAsInt()).findFirst();
                        if (sc != null) sc.setEnabled(true);
                    }


                    realm.commitTransaction();
                }

                GlobalBus.getBus().post(new BusEvents.ModelUpdated("categories"));
                GlobalBus.getBus().post(new BusEvents.ModelUpdated("profile")); // Tambien el perfil porque ahi se visualizan las categorias del profesional
            }

            @Override
            public void successful(String msg) {

            }

            @Override
            public void error(String msg) {

            }
        });
    }

    /**
     * metodo para descargar las categorias asociadas al profesional
     */
    private void downloadMyServices() {
        CategoryInterface restInterface = RestAdapter.getClient(context).create(CategoryInterface.class);
        restInterface.get_my_services().enqueue(new RequestResponseDataJsonArray("categories") {
            @Override
            public void successful(JsonArray dataArray) {
                Realm realm = GlobalRealm.getDefault();
                SubService.setAllEnabledFalse();

                for (JsonElement e : dataArray) {
                    JsonObject object = e.getAsJsonObject();
                    realm.beginTransaction();

                    SubService c = realm.where(SubService.class).equalTo("id", object.get("id").getAsInt()).findFirst();
                    if (c != null) c.setEnabled(true);

                    realm.commitTransaction();
                }
            }

            @Override
            public void successful(String msg) {

            }

            @Override
            public void error(String msg) {

            }
        });
    }

    /**
     * metodo para descargar las categorias asociadas al profesional
     */
    private void downloadMyZones() {
        ZoneInterface restInterface = RestAdapter.getClient(context).create(ZoneInterface.class);
        restInterface.read().enqueue(new RequestResponseDataJsonArray("zones") {
            @Override
            public void successful(JsonArray dataArray) {
                Zone.createOrUpdateArrayBackground(dataArray);
            }

            @Override
            public void successful(String msg) {

            }

            @Override
            public void error(String msg) {

            }
        });
    }

    /**
     * Descargamos los reviews de los profesionales
     *
     * @param page
     */
    private void downloadReviews(int page) {
        ReviewsInterface restInterface = RestAdapter.getClient(context).create(ReviewsInterface.class);
        restInterface.list(page).enqueue(new RequestResponseDataPaginator(page, "reviews") {
            @Override
            public void successful(JsonArray dataArray, int page, boolean hasNextPage) {
                Review.createOrUpdateArrayBackground(dataArray);
                if (hasNextPage) downloadReviews(page);
            }

            @Override
            public void error(String msg) {

            }
        });
    }

    /**
     * Descargamos los appointments del usuario
     */
    private void downloadAppointment(int page) {
        AppointmentInterface restInterface = RestAdapter.getClient(context).create(AppointmentInterface.class);
        restInterface.list(page).enqueue(new RequestResponseDataPaginator(page, "appointment") {
            @Override
            public void successful(JsonArray dataArray, int page, boolean hasNextPage) {
                Appointment.createOrUpdateArrayBackground(dataArray);
                if (hasNextPage) downloadAppointment(page);
            }

            @Override
            public void error(String msg) {

            }
        });
    }

    /**
     * Descargamos los horarios del profesional
     */
    private void downloadSchedule() {
        ProfileInterface restInterface = RestAdapter.getClient(context).create(ProfileInterface.class);
        restInterface.schedule_list().enqueue(new RequestResponseDataJsonArray("schedule_list") {
            @Override
            public void successful(JsonArray dataArray) {
                Schedule.createOrUpdateArrayBackground(dataArray);
            }

            @Override
            public void successful(String msg) {

            }

            @Override
            public void error(String msg) {

            }
        });
    }

    /**
     * Descargamos el dispostivio del usuario
     */
    private void downloadDevice() {
        ProfileInterface restInterface = RestAdapter.getClient(context).create(ProfileInterface.class);
        restInterface.device_list().enqueue(new RequestResponseDataJsonArray("device") {
            @Override
            public void successful(JsonArray dataArray) {
                Device.createOrUpdateArray(dataArray);

                // Recién cuando descargamos y guardamos el device, enviamos de vuelta el sendDevice
                sendDevice();
            }

            @Override
            public void successful(String msg) {

            }

            @Override
            public void error(String msg) {

            }
        });
    }

    /**
     * Descargamos el ranking de los profesionales
     *
     * @param page
     */
    private void downloadRanking(int page) {
        RankingInterface restInterface = RestAdapter.getClient(context).create(RankingInterface.class);
        restInterface.get_ranking(page).enqueue(new RequestResponseDataPaginator(page, "ranking") {
            @Override
            public void successful(JsonArray dataArray, int page, boolean hasNextPage) {
                Professional.createOrUpdateArrayBackground(dataArray);
                if (hasNextPage) downloadRanking(page);
            }

            @Override
            public void error(String msg) {

            }
        });
    }

    /**
     * Método para guardar un dispositivo
     */
    public void sendDevice() {
        JsonObject data = new JsonObject();

        Device d = GlobalRealm.getDefault().where(Device.class).findFirst();
        Call<ResponseData<JsonElement>> call;

        // Si el dispositivo es nulo
        if (d == null) {
            call = RestAdapter.getClient(context).create(ProfileInterface.class).device_create(data);
        } else {
            call = RestAdapter.getClient(context).create(ProfileInterface.class).device_update(data);
            data.addProperty("id", d.getBackend_id());
            data.addProperty("phone_number", User.getUser().getPhone());
        }

        data.addProperty("os", "android");
        data.addProperty("version", android.os.Build.VERSION.SDK_INT);
        data.addProperty("model", android.os.Build.MODEL);
        data.addProperty("push_token", SharedPreferencesUtils.getString(context, Constants.FIREBASE_TOKEN));


        call.enqueue(new RequestResponseDataJsonObject("device") {
            @Override
            public void successful(JsonObject object) {
                JsonArray dataArray = new JsonArray();
                dataArray.add(object);
                Device.createOrUpdateArray(dataArray);
            }

            @Override
            public void successful(String msg) {
            }

            @Override
            public void error(String msg) {
            }
        });
    }

    /**
     * Método para cambiar el estado de un appointment. El CANCELED manejamos en otra parte porque se debe recibir un motivo
     *
     * @param appointment_id
     */
    public static void setAppointmentStatus(AppCompatActivity activity, int appointment_id, int status) {
        JsonObject data = new JsonObject();
        data.addProperty("appointment_id", appointment_id);
        AppointmentInterface restInterface = RestAdapter.getClient(activity).create(AppointmentInterface.class);

        switch (status) {
            case Constants.APPOINTMENT_STATUS.ARRIVED:
                restInterface.set_arrived(data).enqueue(new RequestResponseDataJsonObject("arrived") {
                    @Override
                    public void successful(JsonObject object) {

                    }

                    @Override
                    public void successful(String msg) {
                        AlertGlobal.showCustomSuccess(activity, "Excelente", msg);
                        GlobalBus.getBus().post(new BusEvents.ModelUpdated("appointment"));
                    }

                    @Override
                    public void error(String msg) {
                        AlertGlobal.showCustomError(activity, "Atención", msg);
                    }
                });
                break;
            case Constants.APPOINTMENT_STATUS.IN_PROGRESS:
                restInterface.set_in_progress(data).enqueue(new RequestResponseDataJsonObject("in_progress") {
                    @Override
                    public void successful(JsonObject object) {

                    }

                    @Override
                    public void successful(String msg) {
                        AlertGlobal.showCustomSuccess(activity, "Excelente", msg);
                        GlobalBus.getBus().post(new BusEvents.ModelUpdated("appointment"));
                    }

                    @Override
                    public void error(String msg) {
                        AlertGlobal.showCustomError(activity, "Atención", msg);
                    }
                });
                break;
            case Constants.APPOINTMENT_STATUS.COMPLETED:
                restInterface.set_completed(data).enqueue(new RequestResponseDataJsonObject("completed") {
                    @Override
                    public void successful(JsonObject object) {

                    }

                    @Override
                    public void successful(String msg) {
                        AlertGlobal.showCustomSuccess(activity, "Excelente", msg);
                        GlobalBus.getBus().post(new BusEvents.ModelUpdated("appointment"));
                    }

                    @Override
                    public void error(String msg) {
                        AlertGlobal.showCustomError(activity, "Atención", msg);
                    }
                });
                break;
        }
    }

}
