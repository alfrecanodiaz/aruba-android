package py.com.aruba.clientes.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

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
import py.com.aruba.clientes.ui.auth.rucDialog.ResetPasswordDialog;
import py.com.aruba.clientes.ui.auth.socialAuth.AuthFacebook;
import py.com.aruba.clientes.ui.main.MainActivity;
import py.com.aruba.clientes.utils.Utils;
import py.com.aruba.clientes.utils.UtilsForm;
import py.com.aruba.clientes.utils.alerts.AlertGlobal;
import py.com.aruba.clientes.utils.loading.UtilsLoading;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;

    Context context;
    Realm realm;
    private AuthFacebook authFacebook;
    private UtilsLoading loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Datos básicos del activity
        ButterKnife.bind(this);
        realm = GlobalRealm.getDefault();
        context = this;
        // Datos básicos del activity

        authFacebook = new AuthFacebook(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Si el result viene de facebook
        if (requestCode == authFacebook.RC_SIGN_IN) {
            authFacebook.login = true;
            authFacebook.callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Verificamos si se puede enviar al backend los datos de login
     */
    private void attempLogin() {
        // Reseteamos los errores
        etPassword.setError(null);
        etEmail.setError(null);

        // Guardamos los valores en variables. No es necesario hacerlo but... just for fun
        String password = etPassword.getText().toString();
        String email = etEmail.getText().toString().toLowerCase();

        // Validamos los campos. Si retornan que no es válido, salimos éste método
        if (!UtilsForm.isEmailValid(etEmail, email)) return;
        if (!UtilsForm.isPasswordValid(etPassword, password)) return;

        // Si los campos son válidos, generamos el Json para enviar al servidor
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", email);
        jsonObject.addProperty("password", password);

        // Realizamos la petición al backend
        loginUser(jsonObject);
    }

    /**
     * Método para registrar el usuario
     *
     * @param jsonData
     */
    private void loginUser(JsonObject jsonData) {
        loading = new UtilsLoading(this);
        loading.showLoading();

        ProfileInterface restInterface = RestAdapter.getNoAuthClient(context).create(ProfileInterface.class);

        // Retrofit request
        restInterface.login(jsonData).enqueue(new RequestResponseDataJsonObject( "login") {
            @Override
            public void successful(JsonObject object) {
                User.createOrUpdate(object, context);
                loading.dismissLoading();

                // Le tiramos al usuario al mainActivity
                Intent newIntent = new Intent(context, MainActivity.class);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(newIntent);
                LoginActivity.this.finish();
            }

            @Override
            public void successful(String msg) {
                AlertGlobal.showCustomError(LoginActivity.this, "Atención", msg);
                loading.dismissLoading();
            }

            @Override
            public void error(String msg) {
                AlertGlobal.showCustomError(LoginActivity.this, "Atención", msg);
                loading.dismissLoading();
            }
        });
    }


    @OnClick({R.id.rlBackButton, R.id.btnAccept, R.id.btnFacebook, R.id.tvPassword})
    public void onViewClicked(View view) {
        Utils.preventTwoClick(view);
        switch (view.getId()) {
            case R.id.rlBackButton:
                finish();
                break;
            case R.id.btnAccept:
                attempLogin();
                break;
            case R.id.btnFacebook:
                authFacebook.signIn();
                break;
            case R.id.tvPassword:
                ResetPasswordDialog contactDialog = new ResetPasswordDialog();
                contactDialog.show(getSupportFragmentManager(), "dialog");
                break;
        }
    }
}
