package py.com.aruba.profesionales.data.helpers.request;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import py.com.aruba.profesionales.data.helpers.ErrorResponse;
import py.com.aruba.profesionales.data.helpers.ResponseData;
import py.com.aruba.profesionales.utils.Print;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Esperamos un json object como respuesta o un string con un mensaje espacifico
 */
public abstract class RequestResponseDataJsonObject implements Callback<ResponseData<JsonElement>> {
    private String TAG;


    protected RequestResponseDataJsonObject(String tag) {
        this.TAG = tag;
    }


    @Override
    public void onResponse(Call<ResponseData<JsonElement>> call, Response<ResponseData<JsonElement>> response) {
        try {
            // Status 200 a nivel de la cabecera
            if (response.isSuccessful()) {
                // Si el success que viene en el body es true
                if (response.body() != null && response.body().isSuccess()) {
                    Print.e(TAG, response.body());
                    // Si es successful enviamos el objeto, de lo contrario un string
                    if (response.body().getData().isJsonObject()) {
                        JsonObject object = response.body().getData().getAsJsonObject();
                        successful(object);
                    } else {
                        successful(response.body().getData().getAsString());
                    }
                } else {
                    // Error de lógica/validación
                    String msg = response.body().getData().getAsString();
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
    public void onFailure(Call<ResponseData<JsonElement>> call, Throwable t) {
        // Error a nivel de status
        String msg = t.getMessage();
        error(msg);
        Print.e(TAG, msg);
    }

    // Cuando devuelve correctamente el objeto esperado
    public abstract void successful(JsonObject object);

    // Cuando hay algún mensaje de validación desde el backend
    public abstract void successful(String msg);

    // Cuando hay algún error
    public abstract void error(String msg);
}