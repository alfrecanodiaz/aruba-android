package py.com.aruba.clientes.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.ui.main.MainActivity;
import py.com.aruba.clientes.utils.Constants;
import py.com.aruba.clientes.utils.SharedPreferencesUtils;

public class SelectLoginActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_login);
        overridePendingTransition(0, 0);

        // PART1 - All activities must have this settings
        ButterKnife.bind(this);
        context = this;
        // END PART1


        // Abrimos la pantalla de términos y condiciones la primera vez
        if(!SharedPreferencesUtils.getBoolean(context, Constants.AGREE_TERMS)) {
            Intent intent = new Intent(context, TermsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    }


    @OnClick({R.id.btnAccept, R.id.btnRegister, R.id.btnServices})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnAccept:
                startActivity(new Intent(context, LoginActivity.class));
                break;
            case R.id.btnRegister:
                startActivity(new Intent(context, RegisterActivity.class));
                break;
            case R.id.btnServices:
                startActivity(new Intent(context, MainActivity.class));
                finish();
                break;
        }
    }
}
