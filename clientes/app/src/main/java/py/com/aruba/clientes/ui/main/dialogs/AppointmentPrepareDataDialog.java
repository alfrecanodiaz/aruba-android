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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.eventbus.BusEvents;
import py.com.aruba.clientes.data.eventbus.GlobalBus;
import py.com.aruba.clientes.data.models.Appointment;
import py.com.aruba.clientes.data.models.AppointmentDetails;
import py.com.aruba.clientes.data.models.SubCategory;
import py.com.aruba.clientes.data.models.User;
import py.com.aruba.clientes.data.models.UserAddress;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.ui.appointment.AppointmentReservationActivity;
import py.com.aruba.clientes.ui.main.MainActivity;
import py.com.aruba.clientes.utils.Constants;
import py.com.aruba.clientes.utils.Utils;
import py.com.aruba.clientes.utils.alerts.AlertGlobal;
import py.com.aruba.clientes.utils.dialogs.CustomDialog;

public class AppointmentPrepareDataDialog extends CustomDialog {


    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.tvClientType) TextView tvClientType;
    @BindView(R.id.tvAddress) TextView tvAddress;
    @BindView(R.id.etAddress) TextView etAddress;
    @BindView(R.id.etClientName) EditText etClientName;
    @BindView(R.id.ivProfileMan) ImageView ivProfileMan;
    @BindView(R.id.ivProfileWoman) ImageView ivProfileWoman;
    @BindView(R.id.ivProfileKid) ImageView ivProfileKid;
    @BindView(R.id.llProfileWoman) LinearLayout llProfileWoman;
    @BindView(R.id.llProfileMan) LinearLayout llProfileMan;
    @BindView(R.id.llProfileKid) LinearLayout llProfileKid;
    private String title;
    private int category_id;
    private Realm realm;
    private AppCompatActivity activity;
    private User user;
    private Integer clientType = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_schedule_personalize_data, container, false);
        ButterKnife.bind(this, view);
        activity = (MainActivity) getActivity();
        realm = GlobalRealm.getDefault();
        user = User.getUser(realm);
        GlobalBus.getBus().register(this);
        return view;
    }

    /**
     * Seteamos los datos que vamos a mostrar en el popup
     *
     * @param title
     * @param category_id
     */
    public void setData(String title, int category_id) {
        this.title = title;
        this.category_id = category_id;
    }

    /**
     * Método para setear la dirección por default en el dialogo
     */
    private void setDefaultAddress() {
        etAddress.setText(UserAddress.getDefaultAddressString());
    }

    /**
     * The system calls this only when creating the layout in a dialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // UI
        tvTitle.setText(title);
        setDefaultAddress();
        etClientName.setText(user.getFullName());

        boolean[] client_types = {false, false, false};

        // Obtenemos todas las subcategorias asociadas a esta categoria. Si la subcategoria tiene un tipo de cliente x, ponemos como true en el array
        RealmResults<SubCategory> listSubcategories = realm.where(SubCategory.class).equalTo("category.id", category_id).findAll();
        for(SubCategory sc : listSubcategories){
            if(sc.getClient_type() == Constants.CLIENT_TYPE.MAN) client_types[0] = true;
            if(sc.getClient_type() == Constants.CLIENT_TYPE.WOMAN) client_types[1] = true;
            if(sc.getClient_type() == Constants.CLIENT_TYPE.KIDS) client_types[2] = true;
        }

        // Verificamos si la categoría contiene tipos especificos de clientes, para mostrar y ocultar los iconos
        if (!client_types[0]) llProfileMan.setVisibility(View.GONE);
        if (!client_types[1]) llProfileWoman.setVisibility(View.GONE);
        if (!client_types[2]) llProfileKid.setVisibility(View.GONE);


        // Eliminamos cualquier registro que haya quedado de reservas anteriores y generamos una nueva reserva
        realm.beginTransaction();
        realm.where(AppointmentDetails.class)
                .equalTo("appointment.status", Constants.APPOINTMENT_STATUS.CREATING).or()
                .equalTo("appointment.status", Constants.APPOINTMENT_STATUS.EDITING).or()
                .equalTo("appointment.status", Constants.APPOINTMENT_STATUS.IN_CART)
                .findAll().deleteAllFromRealm();
        realm.where(Appointment.class)
                .equalTo("status", Constants.APPOINTMENT_STATUS.CREATING).or()
                .equalTo("status", Constants.APPOINTMENT_STATUS.EDITING).or()
                .equalTo("status", Constants.APPOINTMENT_STATUS.IN_CART)
                .findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    @OnClick({R.id.llProfileWoman, R.id.llProfileMan, R.id.llProfileKid, R.id.btnAcept, R.id.ivClose})
    public void onViewClicked(View view) {
        Utils.preventTwoClick(view);
        // reseteamos las imagenes
        ivProfileWoman.setImageResource(R.drawable.icon_profile_woman_inactive);
        ivProfileMan.setImageResource(R.drawable.icon_profile_man_inactive);
        ivProfileKid.setImageResource(R.drawable.icon_profile_kid_inactive);
        switch (view.getId()) {

            // Mujeres
            case R.id.llProfileWoman:
                ivProfileWoman.setImageResource(R.drawable.icon_profile_woman_active);
                clientType = Constants.CLIENT_TYPE.WOMAN;
                break;
            // Hombres
            case R.id.llProfileMan:
                ivProfileMan.setImageResource(R.drawable.icon_profile_man_active);
                clientType = Constants.CLIENT_TYPE.MAN;
                break;
            // Niños
            case R.id.llProfileKid:
                ivProfileKid.setImageResource(R.drawable.icon_profile_kid_active);
                clientType = Constants.CLIENT_TYPE.KIDS;
                break;
            case R.id.btnAcept:
                if(validateForm()) return;

                // Una vez que se acepta empezar una nueva reserva, guardamos en realm los datos para ir iterando
                String userAddressString = UserAddress.getDefaultAddressString();
                Integer userAddressId = UserAddress.getDefaultAddressID();

                realm.beginTransaction();
                Appointment appointment = new Appointment();
                appointment.setId(appointment.getId(realm));
                appointment.setCategory(title);
                appointment.setCategory_id(category_id);
                appointment.setStatus(Constants.APPOINTMENT_STATUS.CREATING);
                appointment.setAddress(userAddressString);
                appointment.setAddress_id(userAddressId);
                appointment.setClient(etClientName.getText().toString());
                appointment.setClient_type(clientType);
                realm.copyToRealmOrUpdate(appointment);
                realm.commitTransaction();

                // Abrimos la siguiente pantalla y enviamos como parametro el appointment que se acaba de crear
                Intent intent = new Intent(activity, AppointmentReservationActivity.class);
                intent.putExtra("APPOINTMENT_ID", appointment.getId());
                startActivity(intent);
                AppointmentPrepareDataDialog.this.dismiss();
                break;
            case R.id.ivClose:
                AppointmentPrepareDataDialog.this.dismiss();
                break;
        }
    }

    /**
     * Método para validar los cambios y verificar si se puede continuar. Retornamos true si debe cortar el proceso, de lo contrario retornamos false
     * @return
     */
    private boolean validateForm() {
        if (etAddress.getText().toString().equals("")) {
            AlertGlobal.showMessage(activity, "Atención", "Por favor agregue la dirección del servicio");
            return true;
        }
        if (etClientName.getText().toString().trim().length() == 0) {
            AlertGlobal.showMessage(activity, "Atención", "Por favor ingrese el nombre del cliente que recibirá el servicio.");
            return true;
        }
        if (clientType == 0) {
            AlertGlobal.showMessage(activity, "Atención", "Por favor seleccione el tipo de cliente que recibirá el servicio.");
            return true;
        }
        return false;
    }

    @OnClick(R.id.etAddress)
    void changeAddress() {
        AddressDialog addressDialog = new AddressDialog();
        addressDialog.setData(activity);
        addressDialog.show((activity).getSupportFragmentManager(), "dialog");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onModelChange(BusEvents.ModelUpdated event) {
        if ("address".equals(event.model)) {
            setDefaultAddress();
        }
    }

}
