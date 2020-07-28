package py.com.aruba.clientes.ui.profile.mapsLocation;

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
import androidx.fragment.app.DialogFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.utils.dialogs.CustomDialog;

public class GPSDialog extends CustomDialog {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.dialogFull);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_gps, container, false);
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
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick({R.id.btnAcept, R.id.tvCancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnAcept:
                listeners.onDialogPositiveClick(GPSDialog.this);
                break;
            case R.id.tvCancel:
                GPSDialog.this.dismiss();
                break;
        }
    }

}
