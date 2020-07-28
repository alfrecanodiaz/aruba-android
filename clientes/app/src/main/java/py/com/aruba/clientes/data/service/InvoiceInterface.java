package py.com.aruba.clientes.data.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import py.com.aruba.clientes.data.helpers.ResponseData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface InvoiceInterface {

    @POST("user/factura/list")
    Call<ResponseData<JsonElement>> list();

    @POST("user/factura/create")
    Call<ResponseData<JsonElement>> create(@Body JsonObject data);

    @POST("user/factura/delete")
    Call<ResponseData<JsonElement>> delete(@Body JsonObject data);

    @POST("user/factura/update")
    Call<ResponseData<JsonElement>> update(@Body JsonObject data);
}

