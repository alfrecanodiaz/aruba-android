package py.com.aruba.clientes.ui.professionals;

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
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import jp.wasabeef.recyclerview.adapters.SlideInRightAnimationAdapter;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.eventbus.BusEvents;
import py.com.aruba.clientes.data.eventbus.GlobalBus;
import py.com.aruba.clientes.data.models.Professional;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.ui.professionals.recyclerProfessionals.ProfessionalViewAdapter;
import py.com.aruba.clientes.utils.Utils;

public class ProfessionalFragment extends Fragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.viewEmpty)
    LinearLayout viewEmpty;
    private Realm realm;
    private Context context;
    private AppCompatActivity activity;
    private ProfessionalViewAdapter professionalViewAdapter;
    private int category_id;
    private String query = "";
    private List<Professional> listProfessional;

    static ProfessionalFragment newInstance(int category_id) {
        ProfessionalFragment fragment = new ProfessionalFragment();
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
        activity = (ProfessionalListActivity) getActivity();

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
        Utils.checkEmptyStatus(listProfessionalRealm.size(), viewEmpty);

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

        // Filtramos los ID de las categorias
        if (!query.isEmpty()) {
            listProfessionalRealm = listProfessionalRealm.where()
                    .contains("search_data", query, Case.INSENSITIVE)
                    .findAll();
        }

        listProfessional = realm.copyFromRealm(listProfessionalRealm);
        Utils.checkEmptyStatus(listProfessional.size(), viewEmpty);

        // Recycler View
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        professionalViewAdapter = new ProfessionalViewAdapter(context, listProfessional, activity);
        professionalViewAdapter.setHasStableIds(true);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUIChange(BusEvents.UIUpdate event) {
        if(event.ui.equals("professional"))
        filterDataBase();
        updateRecycler();
    }
}