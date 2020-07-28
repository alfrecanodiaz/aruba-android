package py.com.aruba.profesionales.data.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import py.com.aruba.profesionales.data.helpers.Paginator;
import py.com.aruba.profesionales.data.helpers.ResponseData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ReviewsInterface {


    @POST("professional/review/list_mine")
    Call<ResponseData<Paginator<JsonElement>>> list(@Query("page") Integer page);

    @POST("professional/review/create")
    Call<ResponseData<JsonElement>> create(@Body JsonObject body);
}

