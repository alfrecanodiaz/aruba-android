package py.com.aruba.profesionales.data.helpers.request;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import py.com.aruba.profesionales.data.helpers.ErrorResponse;
import py.com.aruba.profesionales.data.helpers.Paginator;
import py.com.aruba.profesionales.data.helpers.ResponseData;
import py.com.aruba.profesionales.utils.Print;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Clase para manejar de forma global la respuesta obtenida por retrofit
 */
public abstract class RequestResponseDataPaginator implements Callback<ResponseData<Paginator<JsonElement>>> {
    private String TAG;
    private int page;


    /**
     * constructor
     * @param page
     * @param tag
     */
    public RequestResponseDataPaginator(int page, String tag) {
        this.page = page;
        this.TAG = tag;
    }

    @Override
    public void onResponse(Call<ResponseData<Paginator<JsonElement>>> call, Response<ResponseData<Paginator<JsonElement>>> response) {
        try {
            // Status 200 a nivel de la cabecera
            if (response.isSuccessful()) {
                // Si el success que viene en el body es true
                if (response.body() != null && response.body().isSuccess()) {
                    Print.e(TAG, response.body());
                    // Manipulamos los datos recibidos
                    JsonArray data = response.body().getData().getData().getAsJsonArray();
                    successful(data, ++page, response.body().getData().hasNextPage());
                } else {
                    // Error de lógica/validación
                    String msg = response.body().getData().getData().getAsString();
                    error(msg);
                    Print.e(TAG, msg);
                }
            } else {
                // Error a nivel de status
                ErrorResponse errorResponse = ErrorResponse.parseError(response);
                error(errorResponse.message());
                Print.e(TAG, errorResponse.message());
            }
        } catch (Exception e) {
            // Error al hacer parsing
            String msg = "Ocurrió un error inesperado, por favor intente de nuevo." + e.getMessage();
            error(msg);
            Print.e(TAG, e);
        }
    }

    @Override
    public void onFailure(Call<ResponseData<Paginator<JsonElement>>> call, Throwable t) {
        // Error a nivel de status
        String msg = t.getMessage();
        error(msg);
        Print.e(TAG, msg);
    }

    // Forzamos a la instancia a implementar este método, donde le pasamos el dato recibido
    public abstract void successful(JsonArray dataArray, int page, boolean hasNextPage);

    public abstract void error(String msg);
}