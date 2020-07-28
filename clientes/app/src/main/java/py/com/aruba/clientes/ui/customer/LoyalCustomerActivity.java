package py.com.aruba.clientes.ui.customer;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import py.com.aruba.clientes.data.models.SubServicePoints;
import py.com.aruba.clientes.data.models.User;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.ui.customer.recyclerSubServicePoints.CustomerLoyalViewAdapter;
import py.com.aruba.clientes.utils.Constants;
import py.com.aruba.clientes.utils.images.UtilsImage;

public class LoyalCustomerActivity extends AppCompatActivity {

    @BindView(R.id.tvUsername)
    TextView tvUsername;
    @BindView(R.id.ivAvatar)
    ImageView ivAvatar;
    private Context context;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    Realm realm;

    CustomerLoyalViewAdapter customerLoyalViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_customers_loyal);

        // PART1 - All activities must have this settings
        ButterKnife.bind(this);
        context = this;
        realm = GlobalRealm.getDefault();
        // END PART1
        initRecycler();

        setDataContext();
    }

    @OnClick(R.id.rlBackButton)
    public void closeActivity(View view) {
        finish();
    }

    /**
     * Método para setear lo datos relacionados al usuario
     */
    private void setDataContext() {
        User user = User.getUser(realm);
        if (user == null) return;

        tvUsername.setText(String.format("¡Hola %s!", user.getFirst_name()));
        UtilsImage.loadImageCircle(context, ivAvatar, user.getAvatar(), Constants.PLACEHOLDERS.AVATAR);
    }

    /**
     * Método para inicializar el recycler
     */
    private void initRecycler() {

        RealmResults<SubServicePoints> listSubServicePointsRealm = realm.where(SubServicePoints.class).findAll();
        List<SubServicePoints> listSubServicePoints = realm.copyFromRealm(listSubServicePointsRealm);

        // Recycler View
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        customerLoyalViewAdapter = new CustomerLoyalViewAdapter(context, listSubServicePoints);
        recyclerView.setAdapter(customerLoyalViewAdapter);

        // Custom animation
        recyclerView.setAdapter(new SlideInRightAnimationAdapter(customerLoyalViewAdapter));
    }
}
