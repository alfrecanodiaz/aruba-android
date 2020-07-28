package py.com.aruba.profesionales.ui.ranking;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import jp.wasabeef.recyclerview.adapters.SlideInRightAnimationAdapter;
import py.com.aruba.profesionales.R;
import py.com.aruba.profesionales.data.eventbus.BusEvents;
import py.com.aruba.profesionales.data.eventbus.GlobalBus;
import py.com.aruba.profesionales.data.models.Professional;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.ui.ranking.recyclerProfessionals.ProfessionalViewAdapter;


public class RankingFragment extends Fragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private Realm realm;
    private Context context;
    private ProfessionalViewAdapter professionalViewAdapter;
    private int category_id;
    private String query = "";
    private List<Professional> listProfessional;

    static RankingFragment newInstance(int category_id) {
        RankingFragment fragment = new RankingFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("category_id", category_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category_id = getArguments().getInt("category_id");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_professional_list, container, false);
        ButterKnife.bind(this, root);
        realm = GlobalRealm.getDefault();
        context = getActivity();

        initRecycler();
        return root;
    }

    @Override
    public void onAttach(Context context) {
        GlobalBus.getBus().register(this);
        super.onAttach(context);
    }

    /**
     * Cargamos en memoria los datos desde la BD
     */
    private void filterDataBase() {
        // Obtenemos todos los sub-servicios asociados a la categoría
        RealmResults<Professional> listProfessionalRealm = realm.where(Professional.class)
                .contains("categories", String.format("%s;", category_id))
                .findAll();

        // Filtramos si es que el query no está vacio
        if (!query.isEmpty()) {
            listProfessionalRealm = listProfessionalRealm.where()
                    .contains("search_data", query, Case.INSENSITIVE)
                    .findAll();
        }
        listProfessional = realm.copyFromRealm(listProfessionalRealm);
    }

    /**
     * Método para inicializar el recycler
     */
    private void initRecycler() {
        recyclerView.setVisibility(View.VISIBLE);

        // Obtenemos todos los sub-servicios asociados a la categoría
        RealmResults<Professional> listProfessionalRealm = realm.where(Professional.class)
                .contains("categories", String.format("%s;", category_id))
                .findAll();
        listProfessional = realm.copyFromRealm(listProfessionalRealm);

        // Recycler View
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        professionalViewAdapter = new ProfessionalViewAdapter(context, listProfessional, getActivity());
        recyclerView.setAdapter(professionalViewAdapter);

        // Custom animation
        recyclerView.setAdapter(new SlideInRightAnimationAdapter(professionalViewAdapter));
    }

    /**
     * Método para actualizar el recyclerview, luego de filtrarse
     */
    private void updateRecycler() {
        professionalViewAdapter.update(listProfessional);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onQueryChange(BusEvents.ProfessionalListSearchQuery event) {
        this.query = event.string;
        filterDataBase();
        updateRecycler();
    }
}