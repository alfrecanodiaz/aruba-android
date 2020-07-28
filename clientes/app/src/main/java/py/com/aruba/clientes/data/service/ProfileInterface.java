package py.com.aruba.clientes.data.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import py.com.aruba.clientes.data.helpers.ResponseData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ProfileInterface {

    @POST("user/register/email")
    Call<ResponseData<JsonElement>> register(@Body JsonObject data);

    @POST("user/register/facebook")
    Call<ResponseData<JsonElement>> facebook(@Body JsonObject data);

    @POST("user/login")
    Call<ResponseData<JsonElement>> login(@Body JsonObject data);

    @POST("user/me")
    Call<ResponseData<JsonElement>> me();

    @Headers("Content-Type: application/json")
    @POST("api/user/reset_password")
    Call<ResponseData<JsonElement>> reset_password(@Body JsonObject data);

    @POST("user/update")
    Call<ResponseData<JsonElement>> update(@Body JsonObject data);

    @Multipart
    @POST("user/update")
    Call<ResponseData<JsonElement>> modifify_avatar(@Part MultipartBody.Part picture);

    // DEVICE
    @POST("user/device/create")
    Call<ResponseData<JsonElement>> device(@Body JsonObject data);

    @POST("user/device/update")
    Call<ResponseData<JsonElement>> device_update(@Body JsonObject data);

    @POST("user/device/list")
    Call<ResponseData<JsonElement>> list_devices();

    // RUC
    @POST("user/factura/create")
    Call<ResponseData<JsonElement>> ruc_create(@Body JsonObject data);

    @POST("user/factura/update")
    Call<ResponseData<JsonElement>> ruc_update(@Body JsonObject data);

    @POST("user/factura/list")
    Call<ResponseData<JsonElement>> ruc_list();
}

