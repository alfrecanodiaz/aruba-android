package py.com.aruba.clientes.ui.main.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.helpers.RestAdapter;
import py.com.aruba.clientes.data.helpers.request.RequestResponseDataJsonObject;
import py.com.aruba.clientes.data.models.UserAddress;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.data.service.AddressInterface;
import py.com.aruba.clientes.ui.profile.mapsLocation.RegisterMapLocationActivity;
import py.com.aruba.clientes.utils.Utils;
import py.com.aruba.clientes.utils.alerts.AlertGlobal;
import py.com.aruba.clientes.utils.dialogs.CustomDialog;
import py.com.aruba.clientes.utils.loading.UtilsLoading;

public class AddressDialog extends CustomDialog {


    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.rlContentTitle) RelativeLayout rlContentTitle;
    @BindView(R.id.llAddressList) LinearLayout llAddressList;
    @BindView(R.id.btnAcept) Button btnAcept;
    @BindView(R.id.llContent) LinearLayout llContent;
    private Realm realm;
    private AppCompatActivity activity;
    private HashMap<Integer, Button> buttonsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_select_address, container, false);
        ButterKnife.bind(this, view);
        realm = GlobalRealm.getDefault();
        return view;
    }

    /**
     * Seteamos los datos que vamos a mostrar en el popup
     *
     * @param activity
     */
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

        setButtonsList();
    }

    /**
     * Método para setear los botones de sub-categorias
     */
    private void setButtonsList() {
        llAddressList.removeAllViews();
        RealmResults<UserAddress> listAddress = UserAddress.getAll(realm);
        buttonsList = new HashMap<>();

        // Asociamos el view al servicio
        for (UserAddress address : listAddress) {
            final View view = LayoutInflater.from(activity).inflate(R.layout.item_address_button, null);
            Button btnAddress = view.findViewById(R.id.btnAddress);
            btnAddress.setText(address.getFullAddress());

            // Seteamos el button por default y el clickeado
            btnAddress.setOnClickListener(v -> {
                Utils.preventTwoClick(v);
                UserAddress.resetAll(realm);
                UserAddress.setDefault(address.getId());
                updateAddressInBackend(address.getId());

                setButtonStatus();
                AddressDialog.this.dismiss();
            });

            // Guardamos en el listado
            buttonsList.put(address.getId(), btnAddress);

            // Agregamos la vista al contenedor padre
            llAddressList.addView(view);
        }

        setButtonStatus();
    }

    private void setButtonStatus() {
        // Reiniciamos los colores de los demás botones y seteamos el actual
        for (int addressID : buttonsList.keySet()) {
            UserAddress address = realm.where(UserAddress.class).equalTo("id", addressID).findFirst();

            if (address.isIs_default()) {
                buttonsList.get(addressID).setBackground(ContextCompat.getDrawable(activity, R.drawable.button_aqua));
            } else {
                buttonsList.get(addressID).setBackground(ContextCompat.getDrawable(activity, R.drawable.button_silver));
            }
        }
    }

    @OnClick({R.id.btnAcept})
    public void onViewClicked(View view) {
        Utils.preventTwoClick(view);
        switch (view.getId()) {
            case R.id.btnAcept:
                AddressDialog.this.dismiss();
                Intent intent = new Intent(activity, RegisterMapLocationActivity.class);
                startActivity(intent);
                break;
        }
    }


    /**
     * Método para actualizar la dirección default en el backend
     */
    private void updateAddressInBackend(int id){
        // Si los campos son válidos, generamos el Json para enviar al servidor
        JsonObject jsonData = new JsonObject();
        jsonData.addProperty("is_default", true);
        jsonData.addProperty("id", id);


        UtilsLoading loading = new UtilsLoading(activity);
        loading.showLoading();

        AddressInterface restInterface = RestAdapter.getClient(activity).create(AddressInterface.class);
        restInterface.update(jsonData).enqueue(new RequestResponseDataJsonObject( "RegisterAddress") {
            @Override
            public void successful(JsonObject object) {
                loading.dismissLoading();

                JsonArray dataArray = new JsonArray();
                dataArray.add(object);
                UserAddress.createOrUpdateArrayBackground(dataArray);

                AlertGlobal.showCustomSuccess(activity, "Excelente", "Se actualizó correctamente la dirección principal.");
            }

            @Override
            public void successful(String msg) {
                AlertGlobal.showCustomError(activity, "Atención", msg);
                loading.dismissLoading();
            }

            @Override
            public void error(String msg) {
                AlertGlobal.showCustomError(activity, "Atención", msg);
                loading.dismissLoading();
            }
        });
    }
}
