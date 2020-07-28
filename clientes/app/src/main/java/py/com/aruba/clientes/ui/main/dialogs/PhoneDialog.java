package py.com.aruba.clientes.ui.main.dialogs;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.helpers.request.ManageGeneralRequest;
import py.com.aruba.clientes.data.models.User;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.utils.UtilsForm;
import py.com.aruba.clientes.utils.dialogs.CustomDialog;

public class PhoneDialog extends CustomDialog {


    @BindView(R.id.etPhone)
    TextInputEditText etPhone;
    AppCompatActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_phone, container, false);
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


    @OnClick({R.id.btnAcept, R.id.ivClose})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnAcept:
                if (!UtilsForm.isTextValid(etPhone, etPhone.getText().toString())) return;

                // Guardamos el nro de telefono localmente
                Realm realm = GlobalRealm.getDefault();
                realm.beginTransaction();
                User user = realm.where(User.class).findFirst();
                user.setPhone(etPhone.getText().toString());
                realm.commitTransaction();

                // Enviamos el device, una vez que se actualiza el profile
                new ManageGeneralRequest(activity).sendDevice();

                PhoneDialog.this.dismiss();
                break;
            case R.id.ivClose:
                PhoneDialog.this.dismiss();
                break;
        }
    }


}
