package py.com.aruba.clientes.utils;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.github.florent37.viewtooltip.ViewTooltip;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.helpers.RestAdapter;
import py.com.aruba.clientes.data.models.Appointment;
import py.com.aruba.clientes.ui.SplashActivity;
import py.com.aruba.clientes.ui.about.ContactDialog;
import py.com.aruba.clientes.ui.auth.LoginActivity;
import py.com.aruba.clientes.ui.main.MainActivity;
import py.com.aruba.clientes.ui.main.dialogs.PhoneDialog;
import py.com.aruba.clientes.utils.alerts.AlertGlobal;
import py.com.aruba.clientes.utils.dialogs.CustomDialog;
import py.com.aruba.clientes.utils.dialogs.ErrorDialog;
import py.com.aruba.clientes.utils.dialogs.InfoDialog;

public class Utils {

    static DecimalFormat formatea = new DecimalFormat("###,###.##");
    private static ProgressDialog progress;

    /**
     * Verificamos si el usuario está logueado
     *
     * @param context
     * @return
     */
    public static boolean isLogged(Context context) {
        return SharedPreferencesUtils.getBoolean(context, Constants.LOGGED);
    }

    /**
     * Método para prevenir doble click en un elemento
     *
     * @param view
     */
    public static void preventTwoClick(final View view) {
        view.setEnabled(false);
        view.postDelayed(new Runnable() {
            public void run() {
                view.setEnabled(true);
            }
        }, 600);
    }

    /**
     * Método para desloguear al usuario
     */
    public static void logout(final Activity activity) {

        // Mostramos el dialogo personalizado
        ErrorDialog customDialog = new ErrorDialog();
        customDialog.setData("Atención", "¿Está seguro que quiere cerrar sesión?");
        customDialog.setListener(new CustomDialog.ButtonsListener() {
            @Override
            public void onDialogPositiveClick(DialogFragment dialog) {
                // Limpiamos el restadapter
                RestAdapter.setNull();

                // Limpiamos SharedPreferences
                SharedPreferencesUtils.clearSharedPreference(activity);

                // Borramos toda la BD local
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.deleteAll();
                realm.commitTransaction();

                // Mostramos la pantalla de iniciar sesión
                Intent iniciarSesion = new Intent(activity, SplashActivity.class);
                iniciarSesion.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(iniciarSesion);
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
                Print.e("dialogo", "negativo");
            }
        });
        customDialog.show(((AppCompatActivity) activity).getSupportFragmentManager(), "dialog");
    }

    /**
     * Método para obtener separador de miles
     */
    public static String miles(int in) {
        String out = formatea.format(Double.parseDouble(String.valueOf(in))).replace(",", ".");
        return out;
    }

    /**
     * Método para obtener separador de miles
     */
    public static String miles(String in) {
        String out = formatea.format(Double.parseDouble(in)).replace(",", ".");
        return out;
    }

    /**
     * Método para obtener separador de miles
     */
    public static String miles(Double in) {
        String out = formatea.format(in).replace(",", ".");
        return out;
    }

    /**
     * Método para setear la moneda al precio
     */
    public static String priceFormat(String in) {
        String out = "Gs. " + miles(in);
        return out;
    }

    /**
     * Método para setear la moneda al precio
     */
    public static String priceFormat(Double in) {
        String out = "Gs. " + miles(String.valueOf(in));
        return out;
    }

    /**
     * Mostramos un dialogo de espera mientras se ejecutan procesos en background, en este método se pasan ademas del context y el progress, el titulo y el mensaje
     *
     * @param context
     * @param title
     * @param msg
     */
    public static void showLoading(Context context, String title, String msg) {
        if (progress != null) {
            if (!progress.isShowing()) {
                progress = ProgressDialog.show(context, title, msg, true);
            }
        } else {
            progress = ProgressDialog.show(context, title, msg, true);
        }
    }

    /**
     * Mostramos un dialogo de espera mientras se ejecutan procesos en background, en este método solo se pasa el context y el progress
     *
     * @param context
     */
    public static void showLoading(Context context) {
        if (progress != null) {
            if (!progress.isShowing()) {
                progress = ProgressDialog.show(context, "Procesando", "Por favor espere un momento", true);
            }
        } else {
            progress = ProgressDialog.show(context, "Procesando", "Por favor espere un momento", true);
        }
    }

    /**
     * Ocultamos el dialogo de espera que esta asociado al activity/fragment desde donde se llama al showLoading
     */
    public static void dismissLoading() {
        if (progress != null) {
            if (progress.isShowing()) {
                progress.dismiss();
            }
        }
    }

    /**
     * Método para mostrar un mensaje cuando se encuentra sin internet
     */
    public static void showNoInternet(Context context) {
        AlertGlobal.showMessage(context, "Error", "Sin conexión a internet");
    }

    /**
     * Método para setear como negrita el titulo
     *
     * @param s
     * @return
     */
    private static String setStrong(String s) {
        return "*" + s + ":* ";
    }

    /**
     * Metodo para iniciar un activity, finalizar el activity desde el cual se le llamó y eliminar el backstack
     *
     * @param context
     * @param activityClass
     */
    public static void startActivityNoHistory(Activity context, Class activityClass) {
        Intent intent = new Intent(context, activityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    /**
     * Método para mostrar un tooltip de alerta
     *
     * @param view
     * @param msg
     */
    public static void tooltipShow(final View view, final String msg, final ScrollView scroll) {

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                int vLeft = view.getLeft();
                int vRight = view.getRight();
                int sWidth = scroll.getWidth();
                scroll.smoothScrollTo(((vLeft + vRight - sWidth) / 2), 0);

                // Show tooltip
                ViewTooltip
                        .on(view)
                        .autoHide(true, 3000)
                        .clickToHide(true)
                        .corner(30)
                        .color(R.color.mdtp_accent_color_dark)
                        .position(ViewTooltip.Position.TOP)
                        .text(msg)
                        .show();
            }
        });
    }

    /**
     * Método para mostrar un tooltip de alerta
     *
     * @param view
     * @param msg
     */
    public static void tooltipShow(final View view, final String msg) {
        // Show tooltip
        ViewTooltip
                .on(view)
                .autoHide(true, 3000)
                .clickToHide(true)
                .corner(30)
                .color(R.color.mdtp_accent_color_dark)
                .position(ViewTooltip.Position.RIGHT)
                .text(msg)
                .show();
    }

    /**
     * Método para mostrar un tooltip de alerta
     *
     * @param view
     * @param msg
     */
    public static void tooltipShow(final View view, final String msg, ViewTooltip.Position pos) {
        // Show tooltip
        ViewTooltip
                .on(view)
                .autoHide(true, 3000)
                .clickToHide(true)
                .corner(30)
                .color(R.color.mdtp_accent_color_dark)
                .position(pos)
                .text(msg)
                .show();
    }

    /**
     * Vibrar el smartphone
     *
     * @param context
     */
    public static void vibrate(Context context) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(50);
        }
    }

    /**
     * Método para obtener la fecha y hora de forma legible
     */
    public static String getDateTime(String fecha) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM", Locale.ENGLISH);
            Date d = inputFormat.parse(fecha);
            String outPutDate = outputFormat.format(d);

            return outPutDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Método para obtener la fecha y hora de forma legible
     */
    public static String getSimpleDateTime(String fecha) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM HH:mm", Locale.ENGLISH);
            Date d = inputFormat.parse(fecha);
            String outPutDate = outputFormat.format(d);

            return outPutDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Método para obtener la fecha y hora de forma legible
     */
    public static String getSimpleDateTime(Date dateInput) {
        String fecha = String.valueOf(dateInput);

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM HH:mm", Locale.ENGLISH);
            Date d = inputFormat.parse(fecha);
            String outPutDate = outputFormat.format(d);

            return outPutDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Método para obtener la fecha y hora de forma legible
     */
    public static String getDateForSchedule(Date dateInput) {
        String fecha = String.valueOf(dateInput);

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            Date d = inputFormat.parse(fecha);
            String outPutDate = outputFormat.format(d);

            return outPutDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Método para imprimir el token de firebase
     *
     * @param context
     */
    public static void printFirebaseToken(final Context context) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Print.d("token", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();

                    Print.d("token", token);
                    SharedPreferencesUtils.setValue(context, Constants.FIREBASE_TOKEN, token);
                });
    }

    /**
     * A partir de acá los métodos "utils" son propios de éste proyecto
     */

    /**
     * Método para indicar al usuario que la funcionalidad no se encuentra disponible actualmente
     *
     * @param context
     */
    public static void isNotWorkingNow(Context context) {
        Toast.makeText(context, "Esta función estará disponible muy pronto.", Toast.LENGTH_LONG).show();
    }

    public static void loginMessage(final Activity activity) {
        // Mostramos el dialogo personalizado
        ErrorDialog customDialog = new ErrorDialog();
        customDialog.setData("Atención", "Para ver esta sección debe ser un usuario identificado ¿Quiere iniciar sesión?", "Iniciar Sesión");
        customDialog.setListener(new CustomDialog.ButtonsListener() {
            @Override
            public void onDialogPositiveClick(DialogFragment dialog) {
                // Mostramos la pantalla de iniciar sesión
                Intent iniciarSesion = new Intent(activity, LoginActivity.class);
                activity.startActivity(iniciarSesion);
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
                Print.e("dialogo", "negativo");
            }
        });
        customDialog.show(((AppCompatActivity) activity).getSupportFragmentManager(), "dialog");
    }

    public static void disabledUserMessage(final AppCompatActivity activity) {
        // Mostramos el dialogo personalizado
        ErrorDialog customDialog = new ErrorDialog();
        customDialog.setData("Tienes una deuda pendiente", "Para poder agendar una cita, debes cancelar la deuda pendiente. Por favor comunicarse con ARUBA para coordinar el pago.", "Aceptar");
        customDialog.setListener(new CustomDialog.ButtonsListener() {
            @Override
            public void onDialogPositiveClick(DialogFragment dialog) {
                // Mostramos la pantalla de iniciar sesión

                dialog.dismiss();
                ContactDialog contactDialog = new ContactDialog();
                contactDialog.setData(activity);
                contactDialog.show(((AppCompatActivity) activity).getSupportFragmentManager(), "dialog");
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
                Print.e("dialogo", "negativo");
            }
        });
        customDialog.show(((AppCompatActivity) activity).getSupportFragmentManager(), "dialog");
    }

    public static void askForPhone(final AppCompatActivity activity) {
        // Mostramos el dialogo personalizado
        ErrorDialog customDialog = new ErrorDialog();
        customDialog.setData("Atención", "Para poder agendar una cita, debe tener guardado un número de teléfono válido.", "Agregar Número");
        customDialog.setListener(new CustomDialog.ButtonsListener() {
            @Override
            public void onDialogPositiveClick(DialogFragment dialog) {
                // Mostramos la pantalla de iniciar sesión

                dialog.dismiss();
                PhoneDialog contactDialog = new PhoneDialog();
                contactDialog.setData(activity);
                contactDialog.show(((AppCompatActivity) activity).getSupportFragmentManager(), "dialog");
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
                Print.e("dialogo", "negativo");
            }
        });
        customDialog.show(((AppCompatActivity) activity).getSupportFragmentManager(), "dialog");
    }

    public static String getCartAmount(Realm realm) {
        double amount = 0.0;
        realm.beginTransaction();
        RealmResults<Appointment> listAppointments = realm.where(Appointment.class).equalTo("", "").findAll();

        for (Appointment ap : listAppointments) {
            amount += ap.getPrice();
        }
        realm.commitTransaction();
        return priceFormat(amount);
    }

    /**
     * Metodo para verificar si un listado del recyclerview esta vacio o tiene contenido. Si esta vacio mostramos el viewEmpty, de lo contrario oculamos
     *
     * @param size
     * @param viewEmpty
     */
    public static void checkEmptyStatus(int size, LinearLayout viewEmpty) {
        viewEmpty.setVisibility((size > 0 ? View.GONE : View.VISIBLE));
    }
}
