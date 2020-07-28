package py.com.aruba.profesionales.data.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import py.com.aruba.profesionales.data.helpers.Paginator;
import py.com.aruba.profesionales.data.helpers.ResponseData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AppointmentInterface {

    @POST("professional/user/appointment/create")
    Call<ResponseData<JsonElement>> create(@Body JsonObject data);

    @POST("professional/appointment/list")
    Call<ResponseData<Paginator<JsonElement>>> list(@Query("page") Integer page);

    @POST("professional/appointment/arrived")
    Call<ResponseData<JsonElement>> set_arrived(@Body JsonObject data);

    @POST("professional/appointment/in_progress")
    Call<ResponseData<JsonElement>> set_in_progress(@Body JsonObject data);

    @POST("professional/appointment/completed")
    Call<ResponseData<JsonElement>> set_completed(@Body JsonObject data);

    @POST("professional/appointment/canceled")
    Call<ResponseData<JsonElement>> set_canceled(@Body JsonObject data);

}

