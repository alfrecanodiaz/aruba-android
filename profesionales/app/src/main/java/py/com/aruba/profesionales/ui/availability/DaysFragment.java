package py.com.aruba.profesionales.ui.availability;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import jp.wasabeef.recyclerview.adapters.SlideInRightAnimationAdapter;
import py.com.aruba.profesionales.R;
import py.com.aruba.profesionales.data.eventbus.BusEvents;
import py.com.aruba.profesionales.data.eventbus.GlobalBus;
import py.com.aruba.profesionales.data.models.Schedule;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.ui.availability.recyclerDaysAvailable.DaysViewAdapter;
import py.com.aruba.profesionales.utils.Utils;

public class DaysFragment extends Fragment {
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.viewEmpty)
    LinearLayout viewEmpty;


    private Realm realm;
    private DaysViewAdapter daysViewAdapter;
    private int day_id;
    private String query = "";
    private List<Schedule> listSchedule;
    private RealmResults<Schedule> listHorariosRealm;
    private AppCompatActivity activity;

    static DaysFragment newInstance(int day_id) {
        DaysFragment fragment = new DaysFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("day_id", day_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            day_id = getArguments().getInt("day_id");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_days, container, false);
        ButterKnife.bind(this, root);
        realm = GlobalRealm.getDefault();
        activity = (AvailabilityActivity) getActivity();

        initRecycler();
        return root;
    }

    @Override
    public void onAttach(Context context) {
        GlobalBus.getBus().register(this);
        super.onAttach(context);
    }


    /**
     * Método para inicializar el recycler
     */
    private void initRecycler() {
        recyclerView.setVisibility(View.VISIBLE);

        // Obtenemos todos los sub-servicios asociados a la categoría
        listHorariosRealm = realm.where(Schedule.class).equalTo("week_day", day_id).findAll();
        Utils.checkEmptyStatus(listHorariosRealm.size(), viewEmpty);
        listSchedule = realm.copyFromRealm(listHorariosRealm);

        // Recycler View
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(mLayoutManager);
        daysViewAdapter = new DaysViewAdapter(activity, listSchedule, activity);
        recyclerView.setAdapter(daysViewAdapter);

        // Custom animation
        recyclerView.setAdapter(new SlideInRightAnimationAdapter(daysViewAdapter));
    }

    /**
     * Método para actualizar el recyclerview, luego de filtrarse
     */
    private void updateRecycler() {
        // Obtenemos todos los sub-servicios asociados a la categoría
        listHorariosRealm = realm.where(Schedule.class).equalTo("week_day", day_id).findAll();
        Utils.checkEmptyStatus(listHorariosRealm.size(), viewEmpty);
        listSchedule = realm.copyFromRealm(listHorariosRealm);
        // TODO: Ver para hacer algo generico con los recyclers
        daysViewAdapter.update(listSchedule);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onQueryChange(BusEvents.ModelUpdated event) {
        if ("schedules".equals(event.model)) {
            updateRecycler();
        }
    }
}
