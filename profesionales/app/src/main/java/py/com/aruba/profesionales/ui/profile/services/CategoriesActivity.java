package py.com.aruba.profesionales.ui.profile.services;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import jp.wasabeef.recyclerview.adapters.SlideInRightAnimationAdapter;
import py.com.aruba.profesionales.R;
import py.com.aruba.profesionales.data.models.Category;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.ui.profile.services.recyclerCategories.CategoriesViewAdapter;

public class CategoriesActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    private Realm realm;
    private Context context;
    private CategoriesViewAdapter categoriesViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        // PART1 - All activities must have this settings
        ButterKnife.bind(this);
        context = this;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        realm = GlobalRealm.getDefault();
//        GlobalBus.getBus().register(this);

        initRecycler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @OnClick(R.id.rlBackButton)
    public void closeActivity(View view) {
        onBackPressed();
    }

    /**
     * Método para inicializar el recycler
     */
    private void initRecycler() {
        RealmResults<Category> listCategoriesRealm = realm.where(Category.class).findAll();
        List<Category> listCategories = realm.copyFromRealm(listCategoriesRealm);

        // Recycler View
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        categoriesViewAdapter = new CategoriesViewAdapter(context, listCategories);
        recyclerView.setAdapter(categoriesViewAdapter);

        // Custom animation
        recyclerView.setAdapter(new SlideInRightAnimationAdapter(categoriesViewAdapter));
    }

    /**
     * Método para actualizar el recyclerview
     */
    private void updateRecyclerView() {
        RealmResults<Category> listCategoriesRealm = realm.where(Category.class).findAll();
        List<Category> listCategories = realm.copyFromRealm(listCategoriesRealm);
        categoriesViewAdapter.update(listCategories);
    }

}
