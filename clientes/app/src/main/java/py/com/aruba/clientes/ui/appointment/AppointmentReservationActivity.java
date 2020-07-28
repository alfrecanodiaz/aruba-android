package py.com.aruba.clientes.ui.appointment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.eventbus.BusEvents;
import py.com.aruba.clientes.data.eventbus.GlobalBus;
import py.com.aruba.clientes.data.models.Appointment;
import py.com.aruba.clientes.data.models.Category;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.ui.appointment.fragments.Fragment01;
import py.com.aruba.clientes.ui.appointment.fragments.Fragment02;
import py.com.aruba.clientes.ui.appointment.fragments.Fragment03;
import py.com.aruba.clientes.ui.cart.CartActivity;
import py.com.aruba.clientes.utils.Constants;
import py.com.aruba.clientes.utils.NonSwipeableViewPager;
import py.com.aruba.clientes.utils.Utils;
import py.com.aruba.clientes.utils.alerts.AlertGlobal;
import py.com.aruba.clientes.utils.dialogs.CustomDialog;
import py.com.aruba.clientes.utils.dialogs.ErrorDialog;

public class AppointmentReservationActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.tvSubTitle) TextView tvSubTitle;
    @BindView(R.id.tvSubTitleDetail) TextView tvSubTitleDetail;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rlContentTitle) RelativeLayout rlContentTitle;
    @BindView(R.id.rlBar1) RelativeLayout rlBar1;
    @BindView(R.id.rlBar2) RelativeLayout rlBar2;
    @BindView(R.id.rlBar3) RelativeLayout rlBar3;
    @BindView(R.id.tvTotalAmount) TextView tvTotalAmount;
    @BindView(R.id.tvBtnText) TextView tvBtnText;


    private NonSwipeableViewPager viewPager;
    private Realm realm;
    private Category category;
    private Appointment appointment;
    private Context context;
    int actualPage = 0;
    int amount = 0;
    private List<Fragment> listFragment;
    public int APPOINTMENT_ID;
    private double totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_reservation);

        // PART1 - All activities must have this settings
        ButterKnife.bind(this);
        realm = GlobalRealm.getDefault();
        GlobalBus.getBus().register(this);
        context = this;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // END PART1

        APPOINTMENT_ID = getIntent().getIntExtra("APPOINTMENT_ID", 1);
        appointment = realm.where(Appointment.class).equalTo("id", APPOINTMENT_ID).findFirst();
        category = realm.where(Category.class).equalTo("id", appointment.getCategory_id()).findFirst();

        // Obtenemos los datos que se envían desde la pantalla anterior
        listFragment = new ArrayList<>();
        viewPager = findViewById(R.id.pager);

        setTitleText(0);
        tvTotalAmount.setText(Utils.miles(amount));
        configViewPager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        final MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();

        // Obtenemos el evento para saber si se expandio o no
        searchView.setOnSearchClickListener(v -> rlContentTitle.setVisibility(View.INVISIBLE));
        searchView.setOnCloseListener(() -> {
            rlContentTitle.setVisibility(View.VISIBLE);
            return false;
        });

        // Obtenemos el texto que se busca
        searchView.setOnQueryTextListener(this);
        searchView.setIconified(true);
        return true;
    }

    @OnClick({R.id.rlBackButton, R.id.rlButtonBottom})
    public void onViewClicked(View view) {
        Utils.preventTwoClick(view);
        switch (view.getId()) {
            case R.id.rlBackButton:
                listenBackPresesed();
                break;
            case R.id.rlButtonBottom:
                if (actualPage == 0 && !isAmountOverMinimal()) return;
                movePage();
                break;
        }
    }


    // This is for navigation bar back
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            listenBackPresesed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        listenBackPresesed();
    }

    private void listenBackPresesed() {
        if (actualPage == 0) {
            AlertGlobal.showCustomError(AppointmentReservationActivity.this, "Atención", "¿Está seguro que quiere salir del proceso de agendar un servicio?", new CustomDialog.ButtonsListener() {
                @Override
                public void onDialogPositiveClick(DialogFragment dialog) {
                    AppointmentReservationActivity.this.finish();
                }

                @Override
                public void onDialogNegativeClick(DialogFragment dialog) {

                }
            });
        }
        if (actualPage == 1) viewPager.setCurrentItem(0);
        if (actualPage == 2) viewPager.setCurrentItem(1);
    }

    /**
     * Método para pasar de página o mostrar un mensaje de alerta
     */
    private void movePage() {
        boolean canSwipe = false;
        String message = "";
        int next = 0;

        if (actualPage == 0) {
            canSwipe = ((Fragment01) listFragment.get(actualPage)).canSwipe;
            message = ((Fragment01) listFragment.get(actualPage)).message;
            ((Fragment02) listFragment.get(1)).initData();
            next = 1;
        } else if (actualPage == 1) {
            canSwipe = ((Fragment02) listFragment.get(actualPage)).canSwipe;
            message = ((Fragment02) listFragment.get(actualPage)).message;
            next = 2;

            // Obtenemos los profesionales del día actual
//            Calendar today = Calendar.getInstance();
//            ((Fragment02) listFragment.get(actualPage)).onDateSet(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        } else if (actualPage == 2) {
            ((Fragment03) listFragment.get(actualPage)).btnPaid();
            return;
        }

        if (canSwipe) {
            viewPager.setCurrentItem(next);
        } else {
            ErrorDialog customDialog = new ErrorDialog();
            customDialog.setData("Atención", message);
            customDialog.show(((AppointmentReservationActivity) context).getSupportFragmentManager(), "dialog");
        }
    }

    /**
     * Configuramos el viewpager en la página principal
     */
    private void configViewPager() {
        viewPager.setAdapter(new SimplePageAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTitleText(position);
                actualPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setTitleText(int pos) {
        switch (pos) {
            // Servicios
            case 0:
                rlBar1.setBackground(ContextCompat.getDrawable(context, R.drawable.viewpager_step_active));
                rlBar2.setBackground(ContextCompat.getDrawable(context, R.drawable.viewpager_step_inactive));
                rlBar3.setBackground(ContextCompat.getDrawable(context, R.drawable.viewpager_step_inactive));

                // Cabecera
                tvTitle.setVisibility(View.VISIBLE);
                tvTitle.setText("Selecciona el servicio");
                tvBtnText.setText("Continuar");
                break;
            // Profesional
            case 1:
                rlBar1.setBackground(ContextCompat.getDrawable(context, R.drawable.viewpager_step_active));
                rlBar2.setBackground(ContextCompat.getDrawable(context, R.drawable.viewpager_step_active));
                rlBar3.setBackground(ContextCompat.getDrawable(context, R.drawable.viewpager_step_inactive));

                // Cabecera
                tvTitle.setVisibility(View.VISIBLE);
                tvTitle.setText("Selecciona la fecha y horario");
                tvBtnText.setText("Continuar");
                break;
            // Confirmar
            case 2:
                ((Fragment03) listFragment.get(2)).initRecycler();
                ((Fragment03) listFragment.get(2)).setDataContext();

                rlBar1.setBackground(ContextCompat.getDrawable(context, R.drawable.viewpager_step_active));
                rlBar2.setBackground(ContextCompat.getDrawable(context, R.drawable.viewpager_step_active));
                rlBar3.setBackground(ContextCompat.getDrawable(context, R.drawable.viewpager_step_active));

                // Cabecera
                tvTitle.setVisibility(View.VISIBLE);
                tvTitle.setText("Confirmar reserva");
                tvBtnText.setText("Concretar Reserva");
                break;
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        GlobalBus.getBus().post(new BusEvents.ScheduleSearchQuery(query));
        GlobalBus.getBus().post(new BusEvents.ScheduleSearchQueryServices(query));
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        GlobalBus.getBus().post(new BusEvents.ScheduleSearchQuery(newText));
        GlobalBus.getBus().post(new BusEvents.ScheduleSearchQueryServices(newText));
        return false;
    }

    /**
     * A simple pager adapter to setup fragments inside a viewpager
     */
    private class SimplePageAdapter extends FragmentStatePagerAdapter {
        SimplePageAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = Fragment01.newInstance();
                    break;
                case 1:
                    fragment = Fragment02.newInstance();
                    break;
                case 2:
                    fragment = Fragment03.newInstance();
                    break;
            }
            listFragment.add(fragment);
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPageChange(BusEvents.SchedulePage event) {
        viewPager.setCurrentItem(event.pos);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPriceChange(BusEvents.ScheduleAmount event) {
        totalAmount = event.price;
        tvTotalAmount.setText(Utils.miles(event.price));
    }

    private boolean isAmountOverMinimal() {
        if (totalAmount < Constants.MINIMAL_AMOUNT) {
            AlertGlobal.showCustomError(AppointmentReservationActivity.this, "Lo sentimos", "El monto mínimo para solicitar el servicio es de 70.000 Gs.");
            return false;
        } else {
            return true;
        }

    }
}
