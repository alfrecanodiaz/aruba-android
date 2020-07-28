package py.com.aruba.profesionales.ui.balance;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import jp.wasabeef.recyclerview.adapters.SlideInRightAnimationAdapter;
import py.com.aruba.profesionales.R;
import py.com.aruba.profesionales.data.models.Appointment;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.ui.balance.recyclerBalance.BalanceViewAdapter;
import py.com.aruba.profesionales.utils.Constants;
import py.com.aruba.profesionales.utils.TimeUtils;
import py.com.aruba.profesionales.utils.Utils;
import py.com.aruba.profesionales.utils.UtilsImage;

public class BalanceActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    @BindView(R.id.ivProfile)
    ImageView ivProfile;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tvServiceCount)
    TextView tvServiceCount;
    @BindView(R.id.tvTotal)
    TextView tvTotal;
    @BindView(R.id.tvDateFrom)
    TextView tvDateFrom;
    @BindView(R.id.tvDateTo)
    TextView tvDateTo;
    @BindView(R.id.viewEmpty)
    LinearLayout viewEmpty;

    private AppCompatActivity activity;
    private Realm realm;
    private RealmResults<Appointment> listAppointmentRealm;
    private BalanceViewAdapter balanceAdapter;
    private List<Appointment> listAppointment;
    private boolean isDateStart;
    private Date fromDate;
    private Date toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_balance);

        // PART1 - All activities must have this settings
        ButterKnife.bind(this);
        activity = this;
        UtilsImage.loadImageCircle(activity, ivProfile, Utils.getAvatar());
        realm = GlobalRealm.getDefault();
        // END PART1

        initData();
        initRecycler();
        filterDataBase();
    }

    @OnClick(R.id.rlBackButton)
    public void closeActivity(View view) {
        onBackPressed();
    }

    /**
     * Cargamos en memoria los datos desde la BD
     */
    private void filterDataBase() {
        // Obtenemos todos los sub-servicios asociados a la categoría
        listAppointmentRealm = realm.where(Appointment.class)
                .equalTo("status", Constants.APPOINTMENT_STATUS.COMPLETED)
                .between("date", fromDate, toDate)
                .findAll();

        Utils.checkEmptyStatus(listAppointmentRealm.size(), viewEmpty);

        listAppointment = realm.copyFromRealm(listAppointmentRealm);
        balanceAdapter.update(listAppointment);

        // Actualizamos los textos del filtro
        tvServiceCount.setText(String.format("%s citas concretadas en este período", listAppointmentRealm.size()));
        double amount = listAppointmentRealm.sum("professional_earnings").doubleValue();
        tvTotal.setText(Utils.priceFormat(amount));
    }

    /**
     * Método para inicializar el recycler
     */
    private void initRecycler() {
        recyclerView.setVisibility(View.VISIBLE);

        // Obtenemos todos los sub-servicios asociados a la categoría
        listAppointmentRealm = realm.where(Appointment.class)
                .equalTo("status", Constants.APPOINTMENT_STATUS.COMPLETED)
                .between("date", fromDate, toDate)
                .findAll();
        listAppointment = realm.copyFromRealm(listAppointmentRealm);

        // Recycler View
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(mLayoutManager);
        balanceAdapter = new BalanceViewAdapter(listAppointment, activity);
        recyclerView.setAdapter(balanceAdapter);

        // Custom animation
        recyclerView.setAdapter(new SlideInRightAnimationAdapter(balanceAdapter));
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        if (isDateStart) {
            fromDate = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();
            tvDateFrom.setText(TimeUtils.getDateStringReadable(fromDate));

        } else {
            toDate = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();
            tvDateTo.setText(TimeUtils.getDateStringReadable(toDate));
        }

        filterDataBase();
    }


    /**
     * Seteamos los datos dinámicos de la pantalla
     */
    private void initData() {
        // Inicializamos las fechas
        fromDate = new GregorianCalendar(2019, 0, 1).getTime();
        toDate = new GregorianCalendar(2019, 11, 31).getTime();

        tvDateFrom.setText(TimeUtils.getDateStringReadable(fromDate));
        tvDateTo.setText(TimeUtils.getDateStringReadable(toDate));
    }

    @OnClick({R.id.rlContentDateStart, R.id.rlContentDateEnd})
    public void onDateClick(View view) {
        switch (view.getId()) {
            case R.id.rlContentDateStart:
                isDateStart = true;
                Calendar january = Calendar.getInstance();
                january.set(Calendar.MONTH, 0);

                DatePickerDialog dpdStart = DatePickerDialog.newInstance(BalanceActivity.this,
                        january.get(Calendar.YEAR), // Initial year selection
                        january.get(Calendar.MONTH), // Initial month selection
                        january.get(Calendar.DAY_OF_MONTH) // Inital day selection
                );

                dpdStart.show(getSupportFragmentManager(), "Datepickerdialog");
                break;
            case R.id.rlContentDateEnd:
                isDateStart = false;
                Calendar december = Calendar.getInstance();
                december.set(Calendar.MONTH, 11);

                DatePickerDialog dpdEnd = DatePickerDialog.newInstance(BalanceActivity.this,
                        december.get(Calendar.YEAR), // Initial year selection
                        december.get(Calendar.MONTH), // Initial month selection
                        december.get(Calendar.DAY_OF_MONTH) // Inital day selection
                );

                dpdEnd.show(getSupportFragmentManager(), "Datepickerdialog");
                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
