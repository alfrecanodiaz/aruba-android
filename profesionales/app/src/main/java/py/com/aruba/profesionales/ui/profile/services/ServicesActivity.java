package py.com.aruba.profesionales.ui.profile.services;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import py.com.aruba.profesionales.R;
import py.com.aruba.profesionales.data.eventbus.BusEvents;
import py.com.aruba.profesionales.data.eventbus.GlobalBus;
import py.com.aruba.profesionales.data.models.SubCategory;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.utils.Constants;

public class ServicesActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ivLogoToolbar)
    ImageView ivLogoToolbar;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_services);
        // PART1 - All activities must have this settings
        ButterKnife.bind(this);
        realm = GlobalRealm.getDefault();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // END PART1

        int CATEGORY_ID = getIntent().getIntExtra("CATEGORY_ID", 0);

        SectionsPagerAdapter viewPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        RealmResults<SubCategory> listCategories = realm.where(SubCategory.class).equalTo("category.id", CATEGORY_ID).findAll();

        // Iteramos por el listado para obtener todos los tabs
        for (SubCategory subCategory : listCategories) {
            viewPagerAdapter.addFragment(ServicesFragment.newInstance(subCategory.getId()), subCategory.getName() + getClientType(Integer.parseInt(subCategory.getClient_type())));
        }


        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(viewPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.setupWithViewPager(viewPager);
    }

    // Devolvemos el tipo de cliente en string
    private String getClientType(int client_type) {
        switch (client_type) {
            case Constants.CLIENT_TYPE.WOMAN:
                return " - Mujer";
            case Constants.CLIENT_TYPE.MAN:
                return " - Hombre";
            case Constants.CLIENT_TYPE.KIDS:
                return " - Niños";
            default:
                return "";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        final MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();

        // Obtenemos el evento para saber si se expandio o no
        searchView.setOnSearchClickListener(v -> ivLogoToolbar.setVisibility(View.INVISIBLE));
        searchView.setOnCloseListener(() -> {
            ivLogoToolbar.setVisibility(View.VISIBLE);
            return false;
        });

        // Obtenemos el texto que se busca
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                GlobalBus.getBus().post(new BusEvents.ProfessionalListSearchQuery(query));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                GlobalBus.getBus().post(new BusEvents.ProfessionalListSearchQuery(newText));
                return false;
            }
        });
        searchView.setIconified(true);
        return true;
    }


    @OnClick(R.id.rlBackButton)
    public void closeActivity(View view) {
        finish();
    }

    /**
     * Page Adapter with dynamic items
     */
    class SectionsPagerAdapter extends FragmentPagerAdapter {
        List<Fragment> fragmentList = new ArrayList<>();
        List<String> fragmentTitles = new ArrayList<>();

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
        }

        void addFragment(Fragment fragment, String name) {
            fragmentList.add(fragment);
            fragmentTitles.add(name);
        }
    }
}
