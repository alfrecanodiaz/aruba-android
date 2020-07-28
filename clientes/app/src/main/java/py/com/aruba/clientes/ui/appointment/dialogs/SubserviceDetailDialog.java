package py.com.aruba.clientes.ui.appointment.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.models.SubService;
import py.com.aruba.clientes.utils.dialogs.CustomDialog;
import py.com.aruba.clientes.utils.images.UtilsImage;

public class SubserviceDetailDialog extends CustomDialog {
    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.tvDescription) TextView tvDescription;
    @BindView(R.id.ivPicture) ImageView ivPicture;
    private SubService subService;
    private Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_subservice_detail, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    /**
     * Seteamos los datos que vamos a mostrar en el popup
     *  @param subService
     *
     */
    public void setData(Context context, SubService subService) {
        this.context = context;
        this.subService = subService;
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
        tvTitle.setText(subService.getDisplay_name());
        tvDescription.setText(Html.fromHtml(subService.getDescription()));
        tvDescription.setMovementMethod(new ScrollingMovementMethod());
        UtilsImage.loadImageCircle(context, ivPicture, subService.getImage_url());
    }

    @OnClick({R.id.btnAcept, R.id.ivClose})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnAcept:
                listeners.onDialogPositiveClick(SubserviceDetailDialog.this);
                SubserviceDetailDialog.this.dismiss();
                break;
            case R.id.ivClose:
                SubserviceDetailDialog.this.dismiss();
                break;
        }
    }

}
