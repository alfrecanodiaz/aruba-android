package py.com.aruba.profesionales.ui.availability.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import py.com.aruba.profesionales.R;
import py.com.aruba.profesionales.data.helpers.RestAdapter;
import py.com.aruba.profesionales.data.helpers.request.RequestResponseDataJsonObject;
import py.com.aruba.profesionales.data.models.Schedule;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.data.service.ProfileInterface;
import py.com.aruba.profesionales.utils.Print;
import py.com.aruba.profesionales.utils.Utils;
import py.com.aruba.profesionales.utils.alerts.AlertGlobal;
import py.com.aruba.profesionales.utils.dialogs.CustomDialog;

public class ScheduleDialog extends CustomDialog implements TimePickerDialog.OnTimeSetListener {


    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.tvFromHour) TextView tvFromHour;
    @BindView(R.id.tvToHour) TextView tvToHour;

    private Realm realm;
    private AppCompatActivity activity;
    private String title;
    private int day_id;
    private boolean isFromHourPicked;
    private int fromHour = 0;
    private int toHour = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_select_address, container, false);
        ButterKnife.bind(this, view);
        realm = GlobalRealm.getDefault();
        return view;
    }

    /**
     * Seteamos los datos que vamos a mostrar en el popup
     *
     * @param activity
     */
    public void setData(AppCompatActivity activity, int day_id) {
        this.activity = activity;
        this.day_id = day_id;
        this.title = String.format("HORARIO PARA EL %s", getDayString(day_id));
    }

    /**
     * Devolvemos los días en texto
     *
     * @param selected_day
     * @return
     */
    private String getDayString(int selected_day) {
        switch (selected_day) {
            case 0:
                return "DOMINGO";
            case 1:
                return "LUNES";
            case 2:
                return "MARTES";
            case 3:
                return "MIÉRCOLES";
            case 4:
                return "JUEVES";
            case 5:
                return "VIERNES";
            case 6:
                return "SÁBADO";
            default:
                return "LUNES";
        }
    }

    /**
     * The system calls this only when creating the layout in a dialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTitle.setText(title);
    }

    @OnClick({R.id.btnAcept, R.id.llFromHour, R.id.llToHour})
    public void onViewClicked(View view) {
        Utils.preventTwoClick(view);
        switch (view.getId()) {
            case R.id.btnAcept:
                if(fromHour == 0){
                    AlertGlobal.showMessage(activity, "Atención", "Por favor seleccione el horario DESDE");
                    break;
                }
                if(toHour == 0){
                    AlertGlobal.showMessage(activity, "Atención", "Por favor seleccione el horario HASTA");
                    break;
                }
                sendToBackend();
                break;
            case R.id.llFromHour:
                isFromHourPicked = true;
                showTimeSelect();
                break;
            case R.id.llToHour:
                isFromHourPicked = false;
                showTimeSelect();
                break;
        }
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        int realHour = hourOfDay;
        String timeSet = "";
        if (hourOfDay > 12) {
            hourOfDay -= 12;
            timeSet = "PM";
        } else if (hourOfDay == 0) {
            hourOfDay += 12;
            timeSet = "AM";
        } else if (hourOfDay == 12) {
            timeSet = "PM";
        } else {
            timeSet = "AM";
        }

        // Append in a StringBuilder
        String hour = pad(hourOfDay);
        String minutes = pad(minute);

        String selectedTime = String.format("%s:%s %s", hour, minutes, timeSet);

        // Seteamos los datos en los textviews
        if (isFromHourPicked) {
            tvFromHour.setText(selectedTime);
            fromHour = getInSeconds(realHour, minute, second);
        } else {
            tvToHour.setText(selectedTime);
            toHour = getInSeconds(realHour, minute, second);
        }
    }

    /**
     * Método para lanzar el popup de selección de hora
     */
    private void showTimeSelect() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                0,
                false
        );
        timePickerDialog.setThemeDark(false);
        timePickerDialog.setTitle("Schedule de Disponibilidad");
        timePickerDialog.setTimeInterval(1, 15, 60);
        timePickerDialog.setAccentColor(Color.parseColor("#44E8CD"));
        timePickerDialog.setVersion(TimePickerDialog.Version.VERSION_1);
        timePickerDialog.setOnCancelListener(dialog -> Print.e("TimePicker", "Dialog was cancelled"));
        timePickerDialog.show(getFragmentManager(), "Timepickerdialog");
    }

    /**
     * Agregamos un cero adelante de digitos individuales
     *
     * @param digit
     * @return
     */
    private String pad(int digit) {
        return (digit > 9) ? String.valueOf(digit) : ("0" + digit);
    }

    /**
     * Método para retornar los valores en segundos
     *
     * @param hourOfDay
     * @param minute
     * @param second
     * @return
     */
    private int getInSeconds(int hourOfDay, int minute, int second) {
        int result = (hourOfDay * 60 * 60) + (minute * 60) + second;
        return result;
    }

    /**
     * Método para enviar al backend el nuevo horario agendado
     */
    private void sendToBackend() {
        JsonObject data = new JsonObject();
        data.addProperty("week_day", day_id);
        data.addProperty("hour_start", fromHour);
        data.addProperty("hour_end", toHour);

        RestAdapter.getClient(activity).create(ProfileInterface.class).schedule_create(data).enqueue(new RequestResponseDataJsonObject("schedule") {
            @Override
            public void successful(JsonObject object) {
                JsonArray dataArray = new JsonArray();
                dataArray.add(object);
                Schedule.createOrUpdateArrayBackground(dataArray);

                AlertGlobal.showCustomSuccess(activity, "Excelente", "¡Guardado Correctamente!");
                getDialog().dismiss();
            }

            @Override
            public void successful(String msg) {
                AlertGlobal.showCustomSuccess(activity, "Atención", msg);
            }

            @Override
            public void error(String msg) {
                AlertGlobal.showCustomError(activity, "Atención", msg);
            }
        });
    }
}
