package py.com.aruba.clientes.utils.loading;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import py.com.aruba.clientes.R;

public class CustomLoadingActivity extends AppCompatActivity {
    public static String TAG = "CustomLoadingActivity";

    @BindView(R.id.ivBgCircle)
    ImageView ivBgCircle;
    @BindView(R.id.screen)
    RelativeLayout screen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);
        overridePendingTransition(0, 0);
        ButterKnife.bind(this);


        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(3000);
        rotate.setRepeatCount(60);
        rotate.setInterpolator(new LinearInterpolator());
        ivBgCircle.startAnimation(rotate);
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @OnClick(R.id.screen)
    public void onViewClicked() {
        finish();
    }
}
