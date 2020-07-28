package py.com.aruba.clientes.utils.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import py.com.aruba.clientes.R;

public class ErrorDialog extends CustomDialog {
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvBody)
    TextView tvBody;
    @BindView(R.id.btnAcept)
    Button btnAcept;
    private String title;
    private String body;
    private String btn = "ACEPTAR";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_error, container, false);
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
        this.title = title;
        this.body = body;
    }

    /**
     * Seteamos los datos que vamos a mostrar en el popup
     *
     * @param title
     * @param body
     * @param btn
     */
    public void setData(String title, String body, String btn) {
        this.title = title;
        this.body = body;
        this.btn = btn;
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
        tvBody.setText(body);
        btnAcept.setText(btn);
    }

    @OnClick({R.id.btnAcept, R.id.ivClose})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnAcept:
                // Solamente envíamos el evento del listener si no es nulo
                if (listeners != null) listeners.onDialogPositiveClick(ErrorDialog.this);
                ErrorDialog.this.dismiss();
                break;
            case R.id.ivClose:
                ErrorDialog.this.dismiss();
                break;
        }
    }


}