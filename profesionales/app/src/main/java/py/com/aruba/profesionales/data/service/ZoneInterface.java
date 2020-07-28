package py.com.aruba.profesionales.data.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import py.com.aruba.profesionales.data.helpers.ResponseData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ZoneInterface {

    @POST("professional/work_zone/create")
    Call<ResponseData<JsonElement>> create(@Body JsonObject data);

    @POST("professional/work_zone/list")
    Call<ResponseData<JsonElement>> read();

    @POST("professional/work_zone/update")
    Call<ResponseData<JsonElement>> update(@Body JsonObject data);

    @POST("professional/work_zone/delete")
    Call<ResponseData<JsonElement>> delete(@Body JsonObject data);
}

