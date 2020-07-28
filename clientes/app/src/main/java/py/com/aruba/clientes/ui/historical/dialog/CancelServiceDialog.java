package py.com.aruba.clientes.ui.historical.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.eventbus.BusEvents;
import py.com.aruba.clientes.data.eventbus.GlobalBus;
import py.com.aruba.clientes.data.helpers.RestAdapter;
import py.com.aruba.clientes.data.helpers.request.RequestResponseDataJsonObject;
import py.com.aruba.clientes.data.models.Appointment;
import py.com.aruba.clientes.data.models.Professional;
import py.com.aruba.clientes.data.service.AppointmentInterface;
import py.com.aruba.clientes.utils.Constants;
import py.com.aruba.clientes.utils.Utils;
import py.com.aruba.clientes.utils.alerts.AlertGlobal;
import py.com.aruba.clientes.utils.dialogs.CustomDialog;
import py.com.aruba.clientes.utils.images.UtilsImage;

public class CancelServiceDialog extends CustomDialog {

    @BindView(R.id.tvProfessional)
    TextView tvProfessional;
    @BindView(R.id.ivAvatar)
    ImageView ivAvatar;
    @BindView(R.id.etText)
    EditText etText;
    private Appointment appointment;
    private Professional professional;
    private Realm realm;
    private AppCompatActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_service_cancel, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    /**
     * Seteamos los datos que vamos a mostrar en el popup
     *
     * @param appointment
     * @param professional
     * @param activity
     */
    public void setData(Appointment appointment, Professional professional, AppCompatActivity activity) {
        this.appointment = appointment;
        this.activity = activity;
        this.professional = professional;
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

        UtilsImage.loadImageCircle(activity, ivAvatar, professional.getAvatar(), Constants.PLACEHOLDERS.AVATAR);
        tvProfessional.setText(professional.getFullName());
    }

    @OnClick({R.id.btnCancel, R.id.ivClose})
    public void onViewClicked(View view) {
        Utils.preventTwoClick(view);
        switch (view.getId()) {
            case R.id.btnCancel:
                if (etText.getText().toString().isEmpty()) {
                    AlertGlobal.showMessage(activity, "Atención", "Por favor indique el motivo de la cancelación");
                    return;
                }
                cancelService();
                break;
            case R.id.ivClose:
                CancelServiceDialog.this.dismiss();
                break;
        }
    }

    private void cancelService() {
        JsonObject data = new JsonObject();
        data.addProperty("id", appointment.getBackend_id());
        data.addProperty("cancel_reason", etText.getText().toString());
        AppointmentInterface restInterface = RestAdapter.getClient(activity).create(AppointmentInterface.class);

        restInterface.set_canceled(data).enqueue(new RequestResponseDataJsonObject("cancel") {
            @Override
            public void successful(JsonObject object) {
                AlertGlobal.showCustomSuccess(activity, "Correcto", "Cancelado correctamente");
                CancelServiceDialog.this.dismiss();
            }

            @Override
            public void successful(String msg) {
                AlertGlobal.showCustomSuccess(activity, "Correcto", msg);
                CancelServiceDialog.this.dismiss();

                GlobalBus.getBus().post(new BusEvents.UIUpdate("buttonHistoricalAppointmentCancel"));
            }

            @Override
            public void error(String msg) {
                AlertGlobal.showCustomError(activity, "Atención", msg);
            }
        });
    }
}