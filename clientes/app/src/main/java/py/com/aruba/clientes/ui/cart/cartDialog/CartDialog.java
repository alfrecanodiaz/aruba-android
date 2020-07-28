package py.com.aruba.clientes.ui.cart.cartDialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import io.realm.Realm;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.models.User;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.utils.Constants;
import py.com.aruba.clientes.utils.dialogs.CustomDialog;
import py.com.aruba.clientes.utils.images.UtilsImage;

public class CartDialog extends CustomDialog {


    @BindView(R.id.ivAvatar)
    ImageView ivAvatar;
    @BindView(R.id.tvClient)
    TextView tvClient;
    private Realm realm;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_cart_confirm, container, false);
        ButterKnife.bind(this, view);
        realm = GlobalRealm.getDefault();
        return view;
    }

    /**
     * Seteamos los datos que vamos a mostrar en el popup
     *
     * @param context
     */
    public void setData(Context context) {
        this.context = context;
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

        // Seteamos los datos
        User user = realm.where(User.class).findFirst();
        tvClient.setText(user.getFullName());
        UtilsImage.loadImageCircle(context, ivAvatar, user.getAvatar(), Constants.PLACEHOLDERS.AVATAR);

    }

    @OnClick({R.id.btnAcept})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnAcept:
                CartDialog.this.dismiss();
                listeners.onDialogPositiveClick(CartDialog.this);
                break;
        }
    }



}
