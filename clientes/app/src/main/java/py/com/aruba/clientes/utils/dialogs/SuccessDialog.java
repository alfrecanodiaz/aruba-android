package py.com.aruba.clientes.utils.dialogs;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import py.com.aruba.clientes.R;

public class SuccessDialog extends CustomDialog {
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvBody)
    TextView tvBody;
    private String title;
    private String body;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_success, container, false);
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
    }

    @OnClick({R.id.btnAcept, R.id.ivClose})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnAcept:
                listeners.onDialogPositiveClick(SuccessDialog.this);
                SuccessDialog.this.dismiss();
                break;
            case R.id.ivClose:
                SuccessDialog.this.dismiss();
                break;
        }
    }
}
