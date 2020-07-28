package py.com.aruba.profesionales.ui.availability;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import py.com.aruba.profesionales.R;
import py.com.aruba.profesionales.ui.availability.dialogs.ScheduleDialog;
import py.com.aruba.profesionales.utils.Utils;
import py.com.aruba.profesionales.utils.UtilsImage;

public class AvailabilityActivity extends AppCompatActivity {

    @BindView(R.id.ivProfile)
    ImageView ivProfile;
    private Context context;
    private int selected_day = 1; // Empezamos en el 1 = Lunes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_availability);
        // PART1 - All activities must have this settings
        ButterKnife.bind(this);
        context = this;
        UtilsImage.loadImageCircle(context, ivProfile, Utils.getAvatar());
        // END PART1

        SectionsPagerAdapter viewPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Dias a mostrar
        List<String> listDays = new ArrayList<>();
        listDays.add("Lunes");
        listDays.add("Martes");
        listDays.add("Miércoles");
        listDays.add("Jueves");
        listDays.add("Viernes");
        listDays.add("Sábado");
        listDays.add("Domingo");

        // Iteramos por el listado para obtener todos los tabs
        for (int i = 0; i < listDays.size(); i++) {
            viewPagerAdapter.addFragment(DaysFragment.newInstance(i + 1), listDays.get(i));
        }


        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(viewPagerAdapter);
        SmartTabLayout tabs = findViewById(R.id.tabs);
        tabs.setViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selected_day = position + 1; // Sumamos mas 1 porque empieza a contar desde el cero
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick(R.id.rlBackButton)
    public void closeActivity(View view) {
        onBackPressed();
    }

    @OnClick(R.id.btnAddSchedule)
    public void onViewClicked(View view) {
        Utils.preventTwoClick(view);
        ScheduleDialog addressDialog = new ScheduleDialog();
        addressDialog.setData(AvailabilityActivity.this, selected_day);
        addressDialog.show(getSupportFragmentManager(), "dialog");
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
