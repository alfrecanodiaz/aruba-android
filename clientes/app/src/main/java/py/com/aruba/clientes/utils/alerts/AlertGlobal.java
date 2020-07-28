package py.com.aruba.clientes.utils.alerts;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Spanned;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import okhttp3.ResponseBody;
import py.com.aruba.clientes.utils.Print;
import py.com.aruba.clientes.utils.dialogs.CustomDialog;
import py.com.aruba.clientes.utils.dialogs.ErrorDialog;
import py.com.aruba.clientes.utils.dialogs.SuccessDialog;


public class AlertGlobal {

    /**
     * Método para obtener un alert global con el estilo de la app
     *
     * @param activity
     * @param title
     * @param body
     * @param buttons
     */
    public static void showCustomSuccess(AppCompatActivity activity, String title, String body, CustomDialog.ButtonsListener buttons) {
        SuccessDialog customDialog = new SuccessDialog();
        customDialog.setData(title, body);
        customDialog.setListener(buttons);
        customDialog.show(activity.getSupportFragmentManager(), "dialog");
    }

    /**
     * Método para obtener un alert global con el estilo de la app
     *
     * @param activity
     * @param title
     * @param body
     */
    public static void showCustomSuccess(AppCompatActivity activity, String title, String body) {
        SuccessDialog customDialog = new SuccessDialog();
        customDialog.setData(title, body);
        customDialog.setListener(new CustomDialog.ButtonsListener() {
            @Override
            public void onDialogPositiveClick(DialogFragment dialog) {
                dialog.dismiss();
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
                dialog.dismiss();
            }
        });
        customDialog.show(activity.getSupportFragmentManager(), "dialog");
    }

    /**
     * Método para obtener un alert global con el estilo de la app
     *
     * @param activity
     * @param title
     * @param body
     * @param buttons
     */
    public static void showCustomError(AppCompatActivity activity, String title, String body, CustomDialog.ButtonsListener buttons) {
        ErrorDialog customDialog = new ErrorDialog();
        customDialog.setData(title, body);
        customDialog.setListener(buttons);
        customDialog.show(activity.getSupportFragmentManager(), "dialog");
    }

    /**
     * Método para obtener un alert global con el estilo de la app
     *
     * @param activity
     * @param title
     * @param body
     */
    public static void showCustomError(AppCompatActivity activity, String title, String body) {
        ErrorDialog customDialog = new ErrorDialog();
        customDialog.setData(title, body);
        customDialog.setListener(new CustomDialog.ButtonsListener() {
            @Override
            public void onDialogPositiveClick(DialogFragment dialog) {
                dialog.dismiss();
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
                dialog.dismiss();
            }
        });
        customDialog.show(activity.getSupportFragmentManager(), "dialog");
    }

    /**
     * Mostramos un mensaje de error
     *
     * @param context
     */
    public static void showErrorMessage(Context context, Throwable t) {
        if (context == null || ((Activity) context).isFinishing()) {
            return;
        }
        Print.e("Retrofit", t);

        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Atención");
        alertDialog.setMessage("Ocurrió un error inesperado. Por favor intente de nuevo en unos minutos.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    /**
     * Mostramos un simple dialogo de alerta
     *
     * @param context
     * @param title
     * @param msg
     */
    public static void showMessage(Context context, String title, String msg) {
        if (context == null || ((Activity) context).isFinishing()) {
            return;
        }
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    /**
     * Mostramos un simple dialogo de alerta
     *
     * @param context
     * @param title
     * @param msg
     */
    public static void showMessage(Context context, String title, Spanned msg) {
        if (context == null || ((Activity) context).isFinishing()) {
            return;
        }
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    /**
     * Mostramos el error del backend
     *
     * @param context
     * @param responseBody
     */
    public static void showRetrofitError(Context context, ResponseBody responseBody) {
        // Obtenemos el error recibido desde el backend y mostramos como mensaje
        JsonParser parser = new JsonParser();
        String error = null;
        try {
            //Trataremos de mejorar el mensaje de error icon_add_aqua adelante...
            error = responseBody.string();
            Print.e("error", error);
            JsonObject errorObject = parser.parse(error).getAsJsonObject();
            AlertGlobal.showMessage(context, "Error", errorObject.get("data").toString().replace('"', ' '));
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            AlertGlobal.showMessage(context, "Error", "Hubo un problema al tratar de procesar su solicitud. Por favor intente de nuevo icon_add_aqua tarde.");
        } catch (Exception e) {
            e.printStackTrace();
            AlertGlobal.showMessage(context, "Error", "Hubo un problema al tratar de procesar su solicitud. Por favor intente de nuevo icon_add_aqua tarde.");
        }
    }
}
