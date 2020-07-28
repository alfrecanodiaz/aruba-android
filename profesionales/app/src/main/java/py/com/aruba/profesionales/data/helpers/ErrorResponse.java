package py.com.aruba.profesionales.data.helpers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import py.com.aruba.profesionales.utils.Print;
import retrofit2.Response;

public class ErrorResponse {
    private int statusCode;
    private String message = "";

    public ErrorResponse() {
    }

    public int status() {
        return statusCode;
    }

    public String message() {
        return message;
    }

    public static ErrorResponse parseError(Response<?> response) {
        Gson gson = new Gson();
        Type type = new TypeToken<ErrorResponse>() {
        }.getType();

        try {
            ErrorResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
            return errorResponse;
        } catch (Exception e) {
            Print.e("errorDecode", e);
            return new ErrorResponse();
        }
    }
}
