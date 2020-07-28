package py.com.aruba.profesionales.data.service;

import com.google.gson.JsonElement;

import py.com.aruba.profesionales.data.helpers.Paginator;
import py.com.aruba.profesionales.data.helpers.ResponseData;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RankingInterface {

    @POST("professional/reports/average_review")
    Call<ResponseData<Paginator<JsonElement>>> get_ranking(@Query("page") Integer page);
}

