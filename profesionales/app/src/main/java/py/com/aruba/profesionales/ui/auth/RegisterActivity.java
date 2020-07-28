package py.com.aruba.profesionales.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import py.com.aruba.profesionales.R;
import py.com.aruba.profesionales.data.helpers.RestAdapter;
import py.com.aruba.profesionales.data.helpers.request.RequestResponseDataJsonObject;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.data.service.ProfileInterface;
import py.com.aruba.profesionales.ui.auth.socialAuth.AuthFacebook;
import py.com.aruba.profesionales.ui.main.MainActivity;
import py.com.aruba.profesionales.utils.Constants;
import py.com.aruba.profesionales.utils.Print;
import py.com.aruba.profesionales.utils.SharedPreferencesUtils;
import py.com.aruba.profesionales.utils.UtilsForm;
import py.com.aruba.profesionales.utils.alerts.AlertGlobal;
import py.com.aruba.profesionales.utils.dialogs.CustomDialog;
import py.com.aruba.profesionales.utils.dialogs.ErrorDialog;
import py.com.aruba.profesionales.utils.dialogs.SuccessDialog;
import py.com.aruba.profesionales.utils.loading.UtilsLoading;

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
    @BindView(R.id.tvTerms)
    TextView tvTerms;


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
        etPhone.setError(null);

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

        // Retrofit request
        RestAdapter.getNoAuthClient(context).create(ProfileInterface.class).register(jsonData).enqueue(new RequestResponseDataJsonObject("register") {
            @Override
            public void successful(JsonObject object) {
                // No devuelve nada, porque después del registro el usuario debe de validar su mail
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


    @OnClick({R.id.rlBackButton, R.id.btnRegister, R.id.btnFacebook, R.id.tvTerms})
    public void onViewClicked(View view) {
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
            case R.id.tvTerms:
                Intent intent = new Intent(context, TermsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
        }
    }
}
