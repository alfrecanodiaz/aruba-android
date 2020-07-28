package py.com.aruba.profesionales.ui.profile.goals;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import py.com.aruba.profesionales.R;
import py.com.aruba.profesionales.utils.Utils;
import py.com.aruba.profesionales.utils.UtilsImage;

public class GoalsActivity extends AppCompatActivity {

    @BindView(R.id.ivProfile)
    ImageView ivProfile;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_goals);

        // PART1 - All activities must have this settings
        ButterKnife.bind(this);
        context = this;
        UtilsImage.loadImageCircle(context, ivProfile, Utils.getAvatar());
        // END PART1

    }

    @OnClick(R.id.rlBackButton)
    public void closeActivity(View view) {
        onBackPressed();
    }

//    @OnClick(R.id.btnRanking)
//    public void openDialog(View view) {
//        Intent intent = new Intent(context, RankingListActivity.class);
//        startActivity(intent);
//    }

}
