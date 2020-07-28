package py.com.aruba.clientes.data.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import py.com.aruba.clientes.data.helpers.ResponseData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AddressInterface {

    @POST("user/address/list")
    Call<ResponseData<JsonElement>> list();

    @POST("user/address/create")
    Call<ResponseData<JsonElement>> create(@Body JsonObject data);

    @POST("user/address/delete")
    Call<ResponseData<JsonElement>> delete(@Body JsonObject data);

    @POST("user/address/update")
    Call<ResponseData<JsonElement>> update(@Body JsonObject data);
}

