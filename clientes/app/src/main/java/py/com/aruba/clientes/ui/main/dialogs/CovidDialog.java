package py.com.aruba.clientes.ui.main.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.helpers.request.ManageGeneralRequest;
import py.com.aruba.clientes.data.models.User;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.utils.SharedPreferencesUtils;
import py.com.aruba.clientes.utils.UtilsForm;
import py.com.aruba.clientes.utils.alerts.AlertGlobal;
import py.com.aruba.clientes.utils.dialogs.CustomDialog;

public class CovidDialog extends CustomDialog {


    @BindView(R.id.checkboxUno)
    MaterialCheckBox checkboxUno;
    @BindView(R.id.checkboxDos)
    MaterialCheckBox checkboxDos;
    @BindView(R.id.checkboxTres)
    MaterialCheckBox checkboxTres;
    AppCompatActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_covid, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void setData(AppCompatActivity activity) {
        this.activity = activity;
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
    }


    @OnClick({R.id.btnAcept, R.id.ivClose, R.id.tvLink, R.id.tvLink2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnAcept:
                if (checkboxUno.isChecked() && checkboxDos.isChecked() && checkboxTres.isChecked()) {
                    SharedPreferencesUtils.setValue(activity, "covid_form", true);
                    CovidDialog.this.dismiss();
                } else {
                    AlertGlobal.showMessage(activity, "Atención", "Solamente puedes solicitar el servicio si cumples con las condiciones expuestas arriba. ¡Cuidémonos entre todos!");
                }
                break;
            case R.id.ivClose:
                CovidDialog.this.dismiss();
                break;
            case R.id.tvLink:
                Intent iLaboral = new Intent(Intent.ACTION_VIEW);
                iLaboral.setData(Uri.parse("https://www.mspbs.gov.py/dependencias/portal/adjunto/74f0e8-ProtocoloEntornosLaborales01.05.20.pdf"));
                startActivity(iLaboral);
                break;
            case R.id.tvLink2:
                Intent iDomicilio = new Intent(Intent.ACTION_VIEW);
                iDomicilio.setData(Uri.parse("https://www.mspbs.gov.py/dependencias/portal/adjunto/bab140-InstructivoSERVICIOADOMICILIO01.5.20.pdf"));
                startActivity(iDomicilio);
                break;
        }
    }


}
