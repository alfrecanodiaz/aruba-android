package py.com.aruba.clientes.data.helpers;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Gustavo on 01/17/2018.
 */
public class ResponseData<T> {

    @SerializedName("success")
    boolean success;

    @SerializedName("data")
    T data;

    public ResponseData(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
