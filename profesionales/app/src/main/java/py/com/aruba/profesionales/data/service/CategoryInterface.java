package py.com.aruba.profesionales.data.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import py.com.aruba.profesionales.data.helpers.Paginator;
import py.com.aruba.profesionales.data.helpers.ResponseData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CategoryInterface {

    @POST("client/mobile/serviceCategory/list")
    Call<ResponseData<JsonElement>> get_categories();

    @POST("professional/category/list")
    Call<ResponseData<JsonElement>> get_my_categories();

    @POST("client/mobile/service/list")
    Call<ResponseData<Paginator<JsonElement>>> get_sub_services(@Query("page") Integer page);

    @POST("professional/service/list")
    Call<ResponseData<JsonElement>> get_my_services();

    @POST("professional/category/store")
    Call<ResponseData<JsonElement>> store_categories(@Body JsonObject data);

    @POST("professional/service/store")
    Call<ResponseData<JsonElement>> store_services(@Body JsonObject data);
}

