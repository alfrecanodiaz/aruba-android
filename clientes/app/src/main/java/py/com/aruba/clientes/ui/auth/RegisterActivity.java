package py.com.aruba.clientes.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.helpers.RestAdapter;
import py.com.aruba.clientes.data.helpers.request.RequestResponseDataJsonObject;
import py.com.aruba.clientes.data.models.User;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.data.service.ProfileInterface;
import py.com.aruba.clientes.ui.auth.socialAuth.AuthFacebook;
import py.com.aruba.clientes.ui.main.MainActivity;
import py.com.aruba.clientes.utils.Constants;
import py.com.aruba.clientes.utils.SharedPreferencesUtils;
import py.com.aruba.clientes.utils.Utils;
import py.com.aruba.clientes.utils.UtilsForm;
import py.com.aruba.clientes.utils.alerts.AlertGlobal;
import py.com.aruba.clientes.utils.loading.UtilsLoading;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    @BindView(R.id.etFirstName)
    EditText etFirstName;
    @BindView(R.id.etLastName)
    EditText etLastName;
    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.tvTerminos)
    TextView tvTerminos;

    Context context;
    Realm realm;
    private AuthFacebook authFacebook;
    private UtilsLoading loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        realm = GlobalRealm.getDefault();
        context = this;
        authFacebook = new AuthFacebook(this);

//        // Terminos
//        tvTerminos.setText(Html.fromHtml("Al registrarte con nosotros, estás aceptando los <a href='https://aruba.com.py/terminos.php'> términos y condiciones </a>"));
//        tvTerminos.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Si el result viene de facebook
        if (requestCode == authFacebook.RC_SIGN_IN) {
            authFacebook.login = false;
            authFacebook.callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Verificamos si se puede enviar al backend los datos de registro
     */
    private void attemptRegister() {
        // Reseteamos los errores
        etPassword.setError(null);
        etEmail.setError(null);
        etFirstName.setError(null);
        etLastName.setError(null);
//        etPhone.setError(null);

        // Guardamos los valores en variables. No es necesario hacerlo but... just for fun
        String password = etPassword.getText().toString();
        String email = etEmail.getText().toString().toLowerCase();
        String first_name = etFirstName.getText().toString().toLowerCase();
        String last_name = etLastName.getText().toString().toLowerCase();
        String phone = etPhone.getText().toString().toLowerCase();

        // Validamos los campos. Si retornan que no es válido, salimos éste método
        if (!UtilsForm.isTextValid(etFirstName, first_name)) return;
        if (!UtilsForm.isTextValid(etLastName, last_name)) return;
        if (!UtilsForm.isTextValid(etPhone, phone)) return;
        if (!UtilsForm.isEmailValid(etEmail, email)) return;
        if (!UtilsForm.isPasswordValid(etPassword, password)) return;

        // Si los campos son válidos, generamos el Json para enviar al servidor
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", email);
        jsonObject.addProperty("first_name", first_name);
        jsonObject.addProperty("last_name", last_name);
        jsonObject.addProperty("password", password);

        // Realizamos la petición al backend
        requestRegister(jsonObject);
    }

    /**
     * Método para registrar el usuario
     *
     * @param jsonData
     */
    private void requestRegister(JsonObject jsonData) {
        loading = new UtilsLoading(this);
        loading.showLoading();

        ProfileInterface restInterface = RestAdapter.getNoAuthClient(context).create(ProfileInterface.class);

        // Retrofit request
        restInterface.register(jsonData).enqueue(new RequestResponseDataJsonObject("register") {
            @Override
            public void successful(JsonObject object) {
                User.createOrUpdate(object, context);

                // Cerramos el loading
                loading.dismissLoading();

                // Le tiramos al usuario al mainActivity
                Intent newIntent = new Intent(context, MainActivity.class);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(newIntent);
                RegisterActivity.this.finish();
            }

            @Override
            public void successful(String msg) {
                AlertGlobal.showCustomError(RegisterActivity.this, "Atención", msg);
                loading.dismissLoading();
            }

            @Override
            public void error(String msg) {
                AlertGlobal.showCustomError(RegisterActivity.this, "Atención", msg);
                loading.dismissLoading();
            }
        });
    }


    @OnClick({R.id.rlBackButton, R.id.btnRegister, R.id.btnFacebook, R.id.tvTerminos})
    public void onViewClicked(View view) {
        Utils.preventTwoClick(view);
        switch (view.getId()) {
            case R.id.rlBackButton:
                finish();
                break;
            case R.id.btnRegister:
                if (!SharedPreferencesUtils.getBoolean(context, Constants.AGREE_TERMS)) {
                    Intent intent = new Intent(context, TermsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                } else {
                    attemptRegister();
                }
                break;
            case R.id.btnFacebook:
                if (!SharedPreferencesUtils.getBoolean(context, Constants.AGREE_TERMS)) {
                    Intent intent = new Intent(context, TermsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                } else {
                    authFacebook.signIn();
                }
                break;
            case R.id.tvTerminos:
                Intent intent = new Intent(context, TermsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
        }
    }
}
