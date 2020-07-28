package py.com.aruba.clientes.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

import butterknife.ButterKnife;
import io.realm.Realm;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.models.User;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.ui.auth.SelectLoginActivity;
import py.com.aruba.clientes.ui.main.MainActivity;
import py.com.aruba.clientes.utils.Constants;
import py.com.aruba.clientes.utils.SharedPreferencesUtils;
import py.com.aruba.clientes.utils.Utils;

import static py.com.aruba.clientes.utils.Utils.printFirebaseToken;

public class SplashActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        // PART1 - All activities must have this settings
        ButterKnife.bind(this);
        context = this;

        // END PART1
        FirebaseApp.initializeApp(this);
        printFirebaseToken(context);

        // Let's put a delay because it's crash without waiting to load data in memory
        new Handler().postDelayed(this::openActivity, 500);
    }

    //    @OnClick(R.id.rlButton)
    public void openActivity() {
        // Formulario para covid, restablecemos aquí
        SharedPreferencesUtils.setValue(context, "covid_form", false);

        boolean isLogged = Utils.isLogged(context);

        // Si el usuario no está logueado, vamos a crear un usuario anonimo en la BD
        if (!isLogged) {
            Realm realm = GlobalRealm.getDefault();
            if (realm.where(User.class).findFirst() == null) {
                User user = new User();
                user.setFirst_name("Usuario");
                user.setAvatar(Constants.DEFAULT_AVATAR);

                realm.beginTransaction();
                realm.copyToRealmOrUpdate(user);
                realm.commitTransaction();
            }
        }

        // Si existe usuario, le tiramos al main, de lo contrario abrimos la pantalla para que decida que hacer
        startActivity(new Intent(context, (Utils.isLogged(context)) ? MainActivity.class : SelectLoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
