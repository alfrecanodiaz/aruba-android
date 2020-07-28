package py.com.aruba.clientes.ui.promotions;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import jp.wasabeef.recyclerview.adapters.SlideInRightAnimationAdapter;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.models.Promotions;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.ui.promotions.recyclerPromotions.PromotionsViewAdapter;
import py.com.aruba.clientes.utils.Utils;

public class PromotionsActivity extends AppCompatActivity {

    private Context context;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.viewEmpty) LinearLayout viewEmpty;
    private PromotionsViewAdapter promotionsViewAdapter;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_promotions);

        // PART1 - All activities must have this settings
        ButterKnife.bind(this);
        context = this;
        realm = GlobalRealm.getDefault();
        // END PART1

        initRecycler();
    }

    @OnClick(R.id.rlBackButton)
    public void closeActivity(View view) {
        finish();
    }


    /**
     * Método para inicializar el recycler
     */
    private void initRecycler() {

        RealmResults<Promotions> listPromotionsRealm = realm.where(Promotions.class).findAll();
        List<Promotions> listPromotions = realm.copyFromRealm(listPromotionsRealm);

        Utils.checkEmptyStatus(listPromotions.size(), viewEmpty);

        // Recycler View
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        promotionsViewAdapter = new PromotionsViewAdapter(context, listPromotions);
        recyclerView.setAdapter(promotionsViewAdapter);

        // Custom animation
        recyclerView.setAdapter(new SlideInRightAnimationAdapter(promotionsViewAdapter));
    }
}
