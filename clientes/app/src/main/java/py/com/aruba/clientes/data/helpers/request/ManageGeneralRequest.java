package py.com.aruba.clientes.data.helpers.request;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.realm.Realm;
import py.com.aruba.clientes.data.eventbus.BusEvents;
import py.com.aruba.clientes.data.eventbus.GlobalBus;
import py.com.aruba.clientes.data.helpers.ResponseData;
import py.com.aruba.clientes.data.helpers.RestAdapter;
import py.com.aruba.clientes.data.models.Appointment;
import py.com.aruba.clientes.data.models.Category;
import py.com.aruba.clientes.data.models.Device;
import py.com.aruba.clientes.data.models.Professional;
import py.com.aruba.clientes.data.models.Promotions;
import py.com.aruba.clientes.data.models.Review;
import py.com.aruba.clientes.data.models.SubService;
import py.com.aruba.clientes.data.models.User;
import py.com.aruba.clientes.data.models.UserAddress;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.data.service.AddressInterface;
import py.com.aruba.clientes.data.service.AppointmentInterface;
import py.com.aruba.clientes.data.service.CategoryInterface;
import py.com.aruba.clientes.data.service.ProfessionalInterface;
import py.com.aruba.clientes.data.service.ProfileInterface;
import py.com.aruba.clientes.data.service.PromotionInterface;
import py.com.aruba.clientes.utils.Constants;
import py.com.aruba.clientes.utils.SharedPreferencesUtils;
import py.com.aruba.clientes.utils.Utils;
import py.com.aruba.clientes.utils.UtilsNetwork;
import py.com.aruba.clientes.utils.alerts.AlertGlobal;
import retrofit2.Call;

import static py.com.aruba.clientes.utils.UtilsParsing.getElement;

public class ManageGeneralRequest {

    private final Context context;

    public ManageGeneralRequest(Context context) {
        this.context = context;
    }

    public void downloadAll() {
        if (UtilsNetwork.isNetworkAvailable(context)) {
            downloadCategories();

            // Si no está logueado, solo descargamos las categorías
            if (!Utils.isLogged(context)) return;
            me();
            downloadSubServices(1);
        }
    }

    private void downloadCategories() {
        CategoryInterface restInterface = RestAdapter.getNoAuthClient(context).create(CategoryInterface.class);
        restInterface.get_categories().enqueue(new RequestResponseDataJsonArray("categories") {
            @Override
            public void successful(JsonArray dataArray) {
                Category.createOrUpdateArrayBackground(dataArray);

                // Descargamos los profesionales y sevicios una vez que tenemos las categorías
                if (!Utils.isLogged(context)) return;
                downloadSubServices(1);
                downloadProfessional(1);
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
                u.setAvatar(getElement(object, "avatar_url", ""));
                u.setCan_make_appointment(getElement(object, "can_make_appointment", true));

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
     * Descargamos los servicios
     *
     * @param page
     */
    private void downloadSubServices(int page) {
        // Instanciamos el adapter del cliente rest
        CategoryInterface restInterface = RestAdapter.getNoAuthClient(context).create(CategoryInterface.class);

        // Realizamos la petición al backend
        restInterface.get_sub_services(page).enqueue(new RequestResponseDataPaginator(page, "service") {
            @Override
            public void successful(JsonArray dataArray, int page, boolean hasNextPage) {
                SubService.createOrUpdateArrayBackground(dataArray);
                if (hasNextPage){
                    downloadSubServices(page);
                }else{
//                    downloadAppointment(1);
                }
            }

            @Override
            public void error(String msg) {
            }
        });
    }

    /**
     * Descargamos los profesionales
     *
     * @param page
     */
    private void downloadProfessional(int page) {
        ProfessionalInterface restInterface = RestAdapter.getClient(context).create(ProfessionalInterface.class);
        restInterface.get_all_professionals(page).enqueue(new RequestResponseDataJsonArray("professional") {
            @Override
            public void successful(JsonArray dataArray) {
                Professional.createOrUpdateArrayBackground(dataArray);

                // Estos dos endpoints recién descargamos cuando tenemos todos los profesionales guardados
                downloadReviews(1);
                downloadLikes(1);
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
        ProfessionalInterface restInterface = RestAdapter.getClient(context).create(ProfessionalInterface.class);
        restInterface.get_all_reviews(page).enqueue(new RequestResponseDataPaginator(page, "reviews") {
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
     * Descargamos los reviews de los profesionales
     *
     * @param page
     */
    private void downloadLikes(int page) {
        ProfessionalInterface restInterface = RestAdapter.getClient(context).create(ProfessionalInterface.class);
        restInterface.get_all_likes(page).enqueue(new RequestResponseDataPaginator(page, "likes") {
            @Override
            public void successful(JsonArray dataArray, int page, boolean hasNextPage) {

                // Iteramos acá para obtener el ID de los profesionales y marcar como likeado
                Realm realm = GlobalRealm.getDefault();
                for (JsonElement e : dataArray) {
                    JsonObject object = e.getAsJsonObject();
                    realm.beginTransaction();
                    int idProfessional = object.get("professional").getAsJsonObject().get("id").getAsInt();
                    Professional p = realm.where(Professional.class).equalTo("id", idProfessional).findFirst();
                    p.setLiked(true);
                    realm.copyToRealmOrUpdate(p);
                    realm.commitTransaction();
                }

                if (hasNextPage) downloadLikes(page);
            }

            @Override
            public void error(String msg) {

            }
        });
    }

    /**
     * Descargamos las promociones
     *
     * @param page
     */
    private void downloadPromotion(int page) {
        PromotionInterface restInterface = RestAdapter.getClient(context).create(PromotionInterface.class);
        restInterface.get_promotions(page).enqueue(new RequestResponseDataPaginator(page, "promotion") {
            @Override
            public void successful(JsonArray dataArray, int page, boolean hasNextPage) {
                Promotions.createOrUpdateArrayBackground(dataArray);
                if (hasNextPage) downloadPromotion(page);
            }

            @Override
            public void error(String msg) {

            }
        });
    }

    /**
     * Descargamos las direcciones
     */
    private void downloadAddresses() {
        AddressInterface restInterface = RestAdapter.getClient(context).create(AddressInterface.class);
        restInterface.list().enqueue(new RequestResponseDataJsonArray("address") {
            @Override
            public void successful(JsonArray dataArray) {
                UserAddress.createOrUpdateArrayBackground(dataArray);
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
     * Descargamos los appointments del usuario
     */
    public void downloadAppointment(int page) {
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
     * Descargamos el dispostivio del usuario
     */
    private void downloadDevice() {
        ProfileInterface restInterface = RestAdapter.getClient(context).create(ProfileInterface.class);
        restInterface.list_devices().enqueue(new RequestResponseDataJsonArray("device") {
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
     * Descargamos las facturas del usuario
     */
    private void downloadInvoices() {
        ProfileInterface restInterface = RestAdapter.getClient(context).create(ProfileInterface.class);
        restInterface.ruc_list().enqueue(new RequestResponseDataJsonArray("invoice") {
            @Override
            public void successful(JsonArray dataArray) {
                if (dataArray.size() > 0) {
                    JsonObject data = dataArray.get(0).getAsJsonObject();
                    Realm realm = GlobalRealm.getDefault();
                    realm.beginTransaction();
                    User u = User.getUser(realm);
                    u.setRuc_id(getElement(data, "id", 0));
                    u.setRuc(getElement(data, "ruc_number", ""));
                    u.setRazon_social(getElement(data, "social_reason", ""));
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
     * Método para guardar un dispositivo
     */
    public void sendDevice() {
        JsonObject data = new JsonObject();
        Realm realm = GlobalRealm.getDefault();

        Device d = realm.where(Device.class).findFirst();
        Call<ResponseData<JsonElement>> call;

        // Si el dispositivo es nulo
        if (d == null) {
            call = RestAdapter.getClient(context).create(ProfileInterface.class).device(data);
        } else {
            call = RestAdapter.getClient(context).create(ProfileInterface.class).device_update(data);
            data.addProperty("id", d.getBackend_id());
            data.addProperty("phone_number", User.getUser(realm).getPhone());
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


}
