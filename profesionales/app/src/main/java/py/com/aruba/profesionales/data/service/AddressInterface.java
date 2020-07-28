package py.com.aruba.profesionales.data.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import py.com.aruba.profesionales.data.helpers.ResponseData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AddressInterface {

    @POST("professional/user/address/create")
    Call<ResponseData<JsonElement>> create(@Body JsonObject data);

    @POST("professional/user/address/list")
    Call<ResponseData<JsonElement>> list();

    @POST("professional/user/address/update")
    Call<ResponseData<JsonElement>> update(@Body JsonObject data);

    @POST("professional/user/address/delete")
    Call<ResponseData<JsonElement>> delete(@Body JsonObject data);

}

