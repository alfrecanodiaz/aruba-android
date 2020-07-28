package py.com.aruba.clientes.data.helpers.request;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import py.com.aruba.clientes.data.helpers.ErrorResponse;
import py.com.aruba.clientes.data.helpers.ResponseData;
import py.com.aruba.clientes.utils.Print;
import py.com.aruba.clientes.utils.UtilsNetwork;
import py.com.aruba.clientes.utils.alerts.AlertGlobal;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Clase para manejar de forma global la respuesta obtenida por retrofit
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
        String msg = t.getMessage();
        if(t instanceof NoConnectivityException) {
            error("Por favor verifique su conexión a internet.");
        }else{
            // Error a nivel de status
            error(msg);
        }
        Print.e(TAG, msg);
    }
    // Cuando devuelve correctamente el objeto esperado
    public abstract void successful(JsonObject object);
    // Cuando hay algún mensaje de validación desde el backend
    public abstract void successful(String msg);
    // Cuando hay algún error
    public abstract void error(String msg);


}