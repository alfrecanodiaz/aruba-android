package py.com.aruba.profesionales.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

import butterknife.ButterKnife;
import py.com.aruba.profesionales.R;
import py.com.aruba.profesionales.data.models.User;
import py.com.aruba.profesionales.ui.auth.SelectLoginActivity;
import py.com.aruba.profesionales.ui.auth.TermsActivity;
import py.com.aruba.profesionales.ui.main.MainActivity;
import py.com.aruba.profesionales.utils.Constants;
import py.com.aruba.profesionales.utils.SharedPreferencesUtils;

import static py.com.aruba.profesionales.utils.Utils.printFirebaseToken;

public class SplashActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        overridePendingTransition(0, 0);
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


    private void openActivity() {
        Intent intent = new Intent(context, (User.getUser() != null) ? MainActivity.class : SelectLoginActivity.class); // Si existe usuario, le tiramos al main, de lo contrario para que se identifique
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        startActivity(intent);
        finish();
    }
}
