package py.com.aruba.clientes.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import py.com.aruba.clientes.utils.alerts.AlertGlobal;

public class UtilsNetwork {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isNetworkAvailable(AppCompatActivity activity, boolean showMessage) {
        if(showMessage){
            AlertGlobal.showCustomError(activity, "Atención", "Por favor verifique su conexión a internet para poder continuar.");
        }
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    public static boolean isKnownException(Throwable t) {
        return (t instanceof ConnectException
                || t instanceof UnknownHostException
                || t instanceof SocketTimeoutException
                || t instanceof IOException);
    }
}
