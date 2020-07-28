package py.com.aruba.clientes.ui.historical;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import jp.wasabeef.recyclerview.adapters.SlideInRightAnimationAdapter;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.eventbus.BusEvents;
import py.com.aruba.clientes.data.eventbus.GlobalBus;
import py.com.aruba.clientes.data.helpers.request.ManageGeneralRequest;
import py.com.aruba.clientes.data.models.Appointment;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.ui.historical.recyclerHistoricalServices.HistoricalViewAdapter;
import py.com.aruba.clientes.utils.Constants;
import py.com.aruba.clientes.utils.Utils;

public class HistoricalAppointmentsActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.viewEmpty)
    LinearLayout viewEmpty;
    private Context context;

    private HistoricalViewAdapter historicalViewAdapter;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        // PART1 - All activities must have this settings
        ButterKnife.bind(this);
        context = this;
        realm = GlobalRealm.getDefault();
        GlobalBus.getBus().register(this);
        // END PART1

        initRecycler();

        new ManageGeneralRequest(context).downloadAppointment(1);
    }

    @OnClick(R.id.rlBackButton)
    public void closeActivity(View view) {
        finish();
    }

    private void updateRecyclerView() {
        RealmResults<Appointment> listAppointmentRealm = realm.where(Appointment.class)
                .equalTo("status", Constants.APPOINTMENT_STATUS.CREATED).or()
                .equalTo("status", Constants.APPOINTMENT_STATUS.ARRIVED).or()
                .equalTo("status", Constants.APPOINTMENT_STATUS.CANCELED).or()
                .equalTo("status", Constants.APPOINTMENT_STATUS.COMPLETED).or()
                .equalTo("status", Constants.APPOINTMENT_STATUS.IN_PROGRESS)
                .sort("backend_id", Sort.DESCENDING)
                .findAll();

        Utils.checkEmptyStatus(listAppointmentRealm.size(), viewEmpty);

        historicalViewAdapter.update(realm.copyFromRealm(listAppointmentRealm));
    }

    private void initRecycler() {

        RealmResults<Appointment> listAppointmentRealm = realm.where(Appointment.class)
                .equalTo("status", Constants.APPOINTMENT_STATUS.CREATED).or()
                .equalTo("status", Constants.APPOINTMENT_STATUS.ARRIVED).or()
                .equalTo("status", Constants.APPOINTMENT_STATUS.CANCELED).or()
                .equalTo("status", Constants.APPOINTMENT_STATUS.COMPLETED).or()
                .equalTo("status", Constants.APPOINTMENT_STATUS.IN_PROGRESS)
                .sort("backend_id", Sort.DESCENDING)
                .findAll();
        List<Appointment> listAppointments = realm.copyFromRealm(listAppointmentRealm);

        Utils.checkEmptyStatus(listAppointments.size(), viewEmpty);

        // Recycler View
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        historicalViewAdapter = new HistoricalViewAdapter(context, listAppointments);
        recyclerView.setAdapter(historicalViewAdapter);

        // Custom animation
        recyclerView.setAdapter(new SlideInRightAnimationAdapter(historicalViewAdapter));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onModelChange(BusEvents.ModelUpdated event) {
        if ("appointment".equals(event.model)) {
            updateRecyclerView();
        }
    }


}
