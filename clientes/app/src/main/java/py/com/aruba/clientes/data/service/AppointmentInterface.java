package py.com.aruba.clientes.data.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import py.com.aruba.clientes.data.helpers.Paginator;
import py.com.aruba.clientes.data.helpers.ResponseData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AppointmentInterface {

    @POST("user/appointment/create")
    Call<ResponseData<JsonElement>> create(@Body JsonObject data);

    @POST("user/appointment/list")
    Call<ResponseData<Paginator<JsonElement>>> list(@Query("page") Integer page);

    @POST("user/appointment/cancel")
    Call<ResponseData<JsonElement>> set_canceled(@Body JsonObject data);

    @POST("user/appointment/bancard/cancel")
    Call<ResponseData<JsonElement>> cancel_bancard(@Body JsonObject data);


}

