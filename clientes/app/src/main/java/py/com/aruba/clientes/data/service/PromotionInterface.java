package py.com.aruba.clientes.data.service;

import com.google.gson.JsonElement;

import py.com.aruba.clientes.data.helpers.Paginator;
import py.com.aruba.clientes.data.helpers.ResponseData;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PromotionInterface {

    @POST("service/promotion/list")
    Call<ResponseData<Paginator<JsonElement>>> get_promotions(@Query("page") Integer page);
}

