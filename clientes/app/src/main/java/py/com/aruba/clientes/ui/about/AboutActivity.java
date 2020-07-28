package py.com.aruba.clientes.ui.about;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import py.com.aruba.clientes.R;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.tvTerminosUso)
    TextView tvTerminosUso;
    @BindView(R.id.tvPoliticas)
    TextView tvPoliticas;
    @BindView(R.id.tvPreguntas)
    TextView tvPreguntas;
    @BindView(R.id.expandableTerminos)
    ExpandableRelativeLayout expandableTerminos;
    @BindView(R.id.expandablePoliticas)
    ExpandableRelativeLayout expandablePoliticas;
    @BindView(R.id.expandableFaq)
    ExpandableRelativeLayout expandableFaq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        // PART1 - All activities must have this settings
        ButterKnife.bind(this);
        // END PART1

        tvTerminosUso.setText(Html.fromHtml(getString(R.string.terminosUso)));
        tvPoliticas.setText(Html.fromHtml(getString(R.string.politica)));
        tvPreguntas.setText(Html.fromHtml(getString(R.string.preguntas)));
    }


    @OnClick(R.id.rlBackButton)
    public void closeActivity(View view) {
        onBackPressed();
    }

    @OnClick({R.id.rlFaq, R.id.rlTerminos, R.id.rlPoliticas})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rlFaq:
                expandableFaq.toggle();
                break;
            case R.id.rlTerminos:
                expandableTerminos.toggle();
                break;
            case R.id.rlPoliticas:
                expandablePoliticas.toggle();
                break;
        }
    }
}
