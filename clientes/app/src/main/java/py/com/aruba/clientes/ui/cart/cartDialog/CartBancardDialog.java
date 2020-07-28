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
import py.com.aruba.clientes.utils.TimeUtils;
import py.com.aruba.clientes.utils.Utils;
import py.com.aruba.clientes.utils.dialogs.CustomDialog;
import py.com.aruba.clientes.utils.images.UtilsImage;

public class CartBancardDialog extends CustomDialog {


    @BindView(R.id.ivAvatar)
    ImageView ivAvatar;
    @BindView(R.id.tvClient)
    TextView tvClient;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.tvProcessID)
    TextView tvProcessID;
    @BindView(R.id.tvResponse)
    TextView tvResponse;
    @BindView(R.id.tvAmount)
    TextView tvAmount;
    private Realm realm;
    private Context context;

    // Datos para mostrar el formulario de success en el caso de que sea con bancard
    String date;
    String shop_process_id;
    String amount;
    String response_description;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_cart_bancard_confirm, container, false);
        ButterKnife.bind(this, view);
        realm = GlobalRealm.getDefault();
        return view;
    }

    public void setData(Context context, String date, String shop_process_id, String amount, String response_description) {
        this.context = context;
        this.date = TimeUtils.getDateStringReadable(date);
        this.shop_process_id = shop_process_id;
        this.amount = Utils.priceFormat(amount);
        this.response_description = response_description;
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

        tvDate.setText(date);
        tvAmount.setText(amount);
        tvResponse.setText(response_description);
        tvProcessID.setText(shop_process_id);

    }

    @OnClick({R.id.btnAcept})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnAcept:
                CartBancardDialog.this.dismiss();
                listeners.onDialogPositiveClick(CartBancardDialog.this);
                break;
        }
    }


}
