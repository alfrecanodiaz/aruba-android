package py.com.aruba.profesionales.data.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import py.com.aruba.profesionales.data.helpers.ResponseData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ProfileInterface {

    @POST("professional/register/email")
    Call<ResponseData<JsonElement>> register(@Body JsonObject data);

    @POST("professional/user/login")
    Call<ResponseData<JsonElement>> login(@Body JsonObject data);

    @POST("professional/update")
    Call<ResponseData<JsonElement>> modify(@Body JsonObject data);

    @Multipart
    @POST("professional/update")
    Call<ResponseData<JsonElement>> modifify_avatar(@Part MultipartBody.Part picture);

    @POST("professional/register/facebook")
    Call<ResponseData<JsonElement>> facebook(@Body JsonObject data);

    @POST("professional/user/me")
    Call<ResponseData<JsonElement>> me();

    // DEVICE
    @POST("professional/device/update")
    Call<ResponseData<JsonElement>> device_update(@Body JsonObject data);

    @POST("professional/device/create")
    Call<ResponseData<JsonElement>> device_create(@Body JsonObject data);

    @POST("professional/device/list")
    Call<ResponseData<JsonElement>> device_list();


    // SCHEDULE
    @POST("professional/schedule/list")
    Call<ResponseData<JsonElement>> schedule_list();

    @POST("professional/schedule/create")
    Call<ResponseData<JsonElement>> schedule_create(@Body JsonObject data);

    @POST("professional/schedule/delete")
    Call<ResponseData<JsonElement>> schedule_delete(@Body JsonObject data);
}

