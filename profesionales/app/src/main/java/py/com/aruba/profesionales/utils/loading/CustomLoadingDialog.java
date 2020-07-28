package py.com.aruba.profesionales.utils.loading;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import androidx.fragment.app.DialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import py.com.aruba.profesionales.R;

public class CustomLoadingDialog extends DialogFragment {
    public static String TAG = "CustomLoadingActivity";

    @BindView(R.id.ivBgCircle)
    ImageView ivBgCircle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.loading, container, false);
        ButterKnife.bind(this, view);

        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(3000);
        rotate.setRepeatCount(60);
        rotate.setInterpolator(new LinearInterpolator());
        ivBgCircle.startAnimation(rotate);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}
