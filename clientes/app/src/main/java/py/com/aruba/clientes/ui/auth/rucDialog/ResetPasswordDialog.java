package py.com.aruba.clientes.ui.auth.rucDialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.helpers.RestAdapter;
import py.com.aruba.clientes.data.helpers.request.RequestResponseDataJsonObject;
import py.com.aruba.clientes.data.service.ProfileInterface;
import py.com.aruba.clientes.ui.auth.LoginActivity;
import py.com.aruba.clientes.utils.UtilsForm;
import py.com.aruba.clientes.utils.alerts.AlertGlobal;
import py.com.aruba.clientes.utils.dialogs.CustomDialog;
import py.com.aruba.clientes.utils.loading.UtilsLoading;

public class ResetPasswordDialog extends CustomDialog {


    @BindView(R.id.etEmail) TextInputEditText etEmail;
    AppCompatActivity activity;
    private UtilsLoading loading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_reset_password, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    /**
     * Seteamos los datos que vamos a mostrar en el popup
     *
     * @param title
     * @param body
     */
    public void setData(String title, String body) {

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
        activity = (LoginActivity) getActivity();

        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    /**
     * Reseteamos la contraseña
     */
    public void sendBackend() {
        loading = new UtilsLoading(activity);
        loading.showLoading();
        JsonObject data = new JsonObject();
        data.addProperty("email", etEmail.getText().toString());

        ProfileInterface restInterface = RestAdapter.getPasswordClient(activity).create(ProfileInterface.class);
        restInterface.reset_password(data).enqueue(new RequestResponseDataJsonObject("like") {
            @Override
            public void successful(JsonObject object) {
//                AlertGlobal.showCustomSuccess(activity, "Excelente", "Calificado correctamente");
            }

            @Override
            public void successful(String msg) {
                AlertGlobal.showCustomSuccess(activity, "Excelente", msg);
                loading.dismissLoading();
            }

            @Override
            public void error(String msg) {
                AlertGlobal.showCustomError(activity, "Atención", msg);
                loading.dismissLoading();
            }
        });
    }

    @OnClick({R.id.btnAcept, R.id.ivClose})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnAcept:
                if(!UtilsForm.isEmailValid(etEmail, etEmail.getText().toString())) return;
                sendBackend();
                ResetPasswordDialog.this.dismiss();
                break;
            case R.id.ivClose:
                ResetPasswordDialog.this.dismiss();
                break;
        }
    }

}
