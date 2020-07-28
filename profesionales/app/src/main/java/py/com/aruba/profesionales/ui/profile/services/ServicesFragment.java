package py.com.aruba.profesionales.ui.profile.services;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import py.com.aruba.profesionales.data.models.SubService;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.ui.profile.services.recyclerServices.ServicesViewAdapter;

public class ServicesFragment extends Fragment {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private Realm realm;
    private Context context;
    private ServicesViewAdapter serviceViewAdapter;
    private int sub_category_id;
    private String query = "";
    private List<SubService> listSubService;

    static ServicesFragment newInstance(int sub_category_id) {
        ServicesFragment fragment = new ServicesFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("sub_category_id", sub_category_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sub_category_id = getArguments().getInt("sub_category_id");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_services, container, false);
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
//        // Obtenemos todos los sub-servicios asociados a la categoría
//        RealmResults<SubService> listSubServicesRealm = realm.where(SubService.class).findAll();
//
//        // Filtramos si es que el query no está vacio
//        if (!query.isEmpty()) {
//            listSubServicesRealm = listSubServicesRealm.where()
////                    .contains("first_name", query, Case.INSENSITIVE).or()
////                    .contains("last_name", query, Case.INSENSITIVE)
//                    .findAll();
//        }
//        listSubService = realm.copyFromRealm(listSubServicesRealm);
    }

    /**
     * Método para inicializar el recycler
     */
    private void initRecycler() {
        recyclerView.setVisibility(View.VISIBLE);

        // Obtenemos todos los sub-servicios asociados a la categoría
        RealmResults<SubService> listSubServicesRealm = realm.where(SubService.class).equalTo("subCategory.id", sub_category_id).findAll();
        listSubService = realm.copyFromRealm(listSubServicesRealm);

        // Recycler View
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        serviceViewAdapter = new ServicesViewAdapter(context, listSubService, getActivity());
        recyclerView.setAdapter(serviceViewAdapter);

        // Custom animation
        recyclerView.setAdapter(new SlideInRightAnimationAdapter(serviceViewAdapter));
    }

    /**
     * Método para actualizar el recyclerview, luego de filtrarse
     */
    private void updateRecycler() {
//        serviceViewAdapter.update(listSubService);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onQueryChange(BusEvents.ProfessionalListSearchQuery event) {
        this.query = event.string;
        filterDataBase();
        updateRecycler();
    }
}
