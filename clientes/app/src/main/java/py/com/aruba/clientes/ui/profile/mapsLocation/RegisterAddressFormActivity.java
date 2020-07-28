package py.com.aruba.clientes.ui.profile.mapsLocation;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.helpers.ResponseData;
import py.com.aruba.clientes.data.helpers.RestAdapter;
import py.com.aruba.clientes.data.helpers.request.RequestResponseDataJsonObject;
import py.com.aruba.clientes.data.models.UserAddress;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.data.service.AddressInterface;
import py.com.aruba.clientes.utils.Print;
import py.com.aruba.clientes.utils.UtilsForm;
import py.com.aruba.clientes.utils.alerts.AlertGlobal;
import py.com.aruba.clientes.utils.loading.UtilsLoading;
import retrofit2.Call;

public class RegisterAddressFormActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();

    @BindView(R.id.etName)
    TextInputEditText etName;
    @BindView(R.id.etStreet1)
    TextInputEditText etStreet1;
    @BindView(R.id.etStreet2)
    TextInputEditText etStreet2;
    @BindView(R.id.etNumber)
    TextInputEditText etNumber;
    @BindView(R.id.etReferences)
    TextInputEditText etReferences;
    @BindView(R.id.btnAccept)
    Button btnAccept;
    @BindView(R.id.rbCasa)
    RadioButton rbCasa;
    @BindView(R.id.rbOficina)
    RadioButton rbOficina;
    @BindView(R.id.rbOtro)
    RadioButton rbOtro;

    private Context context;
    private String lat = "";
    private String lng = "";
    private boolean is_edit_mode;
    private int address_id;
    private UserAddress addressObject;
    private Realm realm;
    private UtilsLoading loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_form);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        realm = GlobalRealm.getDefault();
        context = this;
        ButterKnife.bind(this);

        is_edit_mode = getIntent().getBooleanExtra("is_edit_mode", false);
        address_id = getIntent().getIntExtra("address_id", 0);
        lat = getIntent().getStringExtra("lat");
        lng = getIntent().getStringExtra("lng");

        // Obtenemos el objeto dirección si estamos en modo edición
        if (is_edit_mode)
            addressObject = realm.where(UserAddress.class).equalTo("backend_id", address_id).findFirst();


        setupUI();
    }

    /**
     * Seteamos los datos del formulario
     */
    private void setupUI() {
        if (is_edit_mode) {
            etName.setText(addressObject.getName());
            etStreet1.setText(addressObject.getStreet1());
            etStreet2.setText(addressObject.getStreet2());
            etNumber.setText(addressObject.getNumber());
            etReferences.setText(addressObject.getReferences());

            btnAccept.setText("Actualizar");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, R.anim.slide_down);
    }

    /**
     * Método para tratar de realizar el login del usuario
     */
    private void attempRegister() {
        // Reseteamos los errores
        etName.setError(null);
        etStreet1.setError(null);
        etStreet2.setError(null);
        etNumber.setError(null);
        etReferences.setError(null);

        // Guardamos los valores en variables. No es necesario hacerlo pero... just for fun (kidding, is better for debug)
        String name = etName.getText().toString();
        String street1 = etStreet1.getText().toString();
        String street2 = etStreet2.getText().toString();
        String number = etNumber.getText().toString();
        String observation = etReferences.getText().toString();

        // Validamos los campos. Si retornan que no es válido, salimos éste método
        if (rbOtro.isChecked()) {
            // solo si otro está checked validamos acá
            if (!UtilsForm.isTextValid(etName, name)) return;
        } else {
            name = (rbCasa.isChecked()) ? "Casa" : "Oficina";
        }
        if (!UtilsForm.isTextValid(etStreet1, street1)) return;
        if (!UtilsForm.isTextValid(etStreet2, street2)) return;
        if (!UtilsForm.isTextValid(etNumber, number)) return;
        if (!UtilsForm.isTextValid(etReferences, observation)) return;

        // Si los campos son válidos, generamos el Json para enviar al servidor
        JsonObject jsonData = new JsonObject();
        jsonData.addProperty("name", name);
        jsonData.addProperty("street1", street1);
        jsonData.addProperty("street2", street2);
        jsonData.addProperty("references", observation);
        jsonData.addProperty("number", number);
        jsonData.addProperty("lat", lat);
        jsonData.addProperty("lng", lng);
        jsonData.addProperty("is_default", true);

        // Si estamos en modo editar, debemos enviar el ID de la dirección
        if (is_edit_mode) jsonData.addProperty("id", addressObject.getBackend_id());


        // Realizamos la petición al backend
        sendToServer(jsonData);
    }


    /**
     * Método para realizar un request al API
     */
    public void sendToServer(JsonObject jsonObject) {
        loading = new UtilsLoading(this);
        loading.showLoading();

        AddressInterface restInterface = RestAdapter.getClient(context).create(AddressInterface.class);
        Call<ResponseData<JsonElement>> call;
        if (is_edit_mode) {
            call = restInterface.update(jsonObject);
        } else {
            call = restInterface.create(jsonObject);
        }

        call.enqueue(new RequestResponseDataJsonObject("RegisterAddress") {
            @Override
            public void successful(JsonObject object) {
                loading.dismissLoading();

                JsonArray dataArray = new JsonArray();
                dataArray.add(object);

                UserAddress.resetAll(realm);
                UserAddress.createOrUpdateArrayBackground(dataArray);
                RegisterMapLocationActivity.CLOSE_ACTIVITY = true; // Hacemos que también se cierre el mapa
                RegisterAddressFormActivity.this.finish();
            }

            @Override
            public void successful(String msg) {
                AlertGlobal.showCustomError(RegisterAddressFormActivity.this, "Atención", msg);
                loading.dismissLoading();
            }

            @Override
            public void error(String msg) {
                AlertGlobal.showCustomError(RegisterAddressFormActivity.this, "Atención", msg);
                loading.dismissLoading();
            }
        });
    }

    @OnClick({R.id.btnAccept, R.id.rlBackButton, R.id.rgButtons})
    protected void allOnClick(View v) {
        switch (v.getId()) {
            case R.id.btnAccept:
                attempRegister();
                break;
            case R.id.rlBackButton:
                finish();
                break;
        }
    }

}
