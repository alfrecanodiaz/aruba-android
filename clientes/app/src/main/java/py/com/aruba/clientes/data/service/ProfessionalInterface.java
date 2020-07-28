package py.com.aruba.clientes.data.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import py.com.aruba.clientes.data.helpers.Paginator;
import py.com.aruba.clientes.data.helpers.ResponseData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ProfessionalInterface {

//    @POST("user/professional/list")
//    Call<ResponseData<JsonElement>> get_filtered_professionals(@Query("page") Integer page, @Body JsonObject data);

    @POST("user/professional/listAvailable")
    Call<ResponseData<JsonElement>> get_filtered_professionals(@Query("page") Integer page, @Body JsonObject data);

    @POST("user/professional/list")
    Call<ResponseData<JsonElement>> get_all_professionals(@Query("page") Integer page);

    @POST("user/review/list")
    Call<ResponseData<Paginator<JsonElement>>> get_all_reviews(@Query("page") Integer page);

    @POST("user/review/create")
    Call<ResponseData<JsonElement>> review_professional(@Body JsonObject data);

    @POST("user/liked")
    Call<ResponseData<Paginator<JsonElement>>> get_all_likes(@Query("page") Integer page);

    @POST("user/professional/like")
    Call<ResponseData<JsonElement>> set_like(@Body JsonObject data);
}

