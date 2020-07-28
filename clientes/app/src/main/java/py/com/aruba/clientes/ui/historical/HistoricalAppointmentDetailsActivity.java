package py.com.aruba.clientes.ui.historical;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
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
import jp.wasabeef.recyclerview.adapters.SlideInRightAnimationAdapter;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.eventbus.BusEvents;
import py.com.aruba.clientes.data.eventbus.GlobalBus;
import py.com.aruba.clientes.data.helpers.request.ManageGeneralRequest;
import py.com.aruba.clientes.data.models.Appointment;
import py.com.aruba.clientes.data.models.AppointmentDetails;
import py.com.aruba.clientes.data.models.Professional;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.ui.historical.dialog.CancelServiceDialog;
import py.com.aruba.clientes.ui.historical.recyclerHistoricalSubServices.HistoricalSubServiceViewAdapter;
import py.com.aruba.clientes.ui.main.dialogs.ReviewDialog;
import py.com.aruba.clientes.utils.Constants;
import py.com.aruba.clientes.utils.TimeUtils;
import py.com.aruba.clientes.utils.Utils;
import py.com.aruba.clientes.utils.alerts.AlertGlobal;
import py.com.aruba.clientes.utils.dialogs.CustomDialog;
import py.com.aruba.clientes.utils.images.UtilsImage;


public class HistoricalAppointmentDetailsActivity extends AppCompatActivity {


    @BindView(R.id.tvProfessional)
    TextView tvProfessional;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.tvHour)
    TextView tvHour;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.tvTotal)
    TextView tvTotal;
    @BindView(R.id.tvPaymentMethod)
    TextView tvPaymentMethod;
    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.ivAvatar)
    ImageView ivAvatar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.btnCancel)
    Button btnCancel;
    @BindView(R.id.btnReview)
    Button btnReview;
    private HistoricalSubServiceViewAdapter historicalSubServiceViewAdapter;
    private HistoricalAppointmentDetailsActivity activity;
    private Realm realm;
    private Appointment appointment;
    private Professional professional;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);
        ButterKnife.bind(this);
        activity = this;

        realm = GlobalRealm.getDefault();
        int appointment_id = getIntent().getIntExtra("ID", 0);

        GlobalBus.getBus().register(this);

        appointment = Appointment.getByBackendID(appointment_id);
        professional = Professional.getByID(appointment.getProfessional_id());
        setData();
        initRecycler();
    }

    /**
     * Método para setear los datos de las pantallas
     */
    private void setData() {
        if (professional != null) {
            UtilsImage.loadImageCircle(activity, ivAvatar, professional.getAvatar(), Constants.PLACEHOLDERS.AVATAR);
            tvProfessional.setText(String.format("Profesional: %s", professional.getFullName()));
        }
        tvDate.setText(String.format("Fecha: %s", TimeUtils.getDateStringReadable(appointment.getDate())));
        tvHour.setText(String.format("Hora: %s", appointment.getHour_start_pretty()));
        tvAddress.setText(String.format("%s", appointment.getAddress()));
        tvTotal.setText(String.format("Total: %s", Utils.priceFormat(appointment.getPrice())));
        tvPaymentMethod.setText(String.format("Método de Pago: %s", appointment.getPayment_method()));
        tvTime.setText(String.format("Tiempo estimado: %s", Appointment.getDurationString(appointment.getDuration())));

        updateButtonStatus();
    }

    private void initRecycler() {

        RealmResults<AppointmentDetails> listAppointmentRealm = realm.where(AppointmentDetails.class)
                .equalTo("appointment.id", appointment.getId()).findAll();

        List<AppointmentDetails> listAppointments = realm.copyFromRealm(listAppointmentRealm);

        // Recycler View
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(mLayoutManager);
        historicalSubServiceViewAdapter = new HistoricalSubServiceViewAdapter(activity, listAppointments);
        recyclerView.setAdapter(historicalSubServiceViewAdapter);

        // Custom animation
        recyclerView.setAdapter(new SlideInRightAnimationAdapter(historicalSubServiceViewAdapter));
    }

    @OnClick({R.id.btnReview, R.id.btnCancel})
    public void onViewClicked(View view) {
        Utils.preventTwoClick(view);
        switch (view.getId()) {
            case R.id.btnReview:
                ReviewDialog schedule = new ReviewDialog();
                schedule.setData(professional, activity);
                schedule.show(getSupportFragmentManager(), "dialog");
                break;
            case R.id.btnCancel:
                checkCancelationTime();
                break;
        }
    }

    private void updateButtonStatus() {
        // Si el estado es diferente a is_created, no mostramos la opción de cancelar
        if (appointment.getStatus() != Constants.APPOINTMENT_STATUS.CREATED)
            btnCancel.setVisibility(View.GONE);
        // Solamente permitimos calificar al profesional cuando el estado es completed
        if (appointment.getStatus() == Constants.APPOINTMENT_STATUS.COMPLETED)
            btnReview.setVisibility(View.VISIBLE);
    }

    /**
     * Método para verificar si aún no pasaron las 3hrs de anticipación para cancelar una reserva
     */
    private void checkCancelationTime() {

        int actualTime = TimeUtils.getActualTimeInSeconds();
        int reservationTime = appointment.getFrom_hour();
        int threeHoursInSeconds = 10800;

        // Si, la resta entre el tiemepo en el que debería empezar la reserva es menor a tres horas en segundos
        if (TimeUtils.isSameDay(appointment.getDate()) && (reservationTime - actualTime) < threeHoursInSeconds) {
            AlertGlobal.showCustomError(HistoricalAppointmentDetailsActivity.this, "¡ATENCIÓN!", "Si cancelas tu cita en menos de tres horas se cobrará el 100% del total del servicio solicitado.", new CustomDialog.ButtonsListener() {
                @Override
                public void onDialogPositiveClick(DialogFragment dialog) {
                    CancelServiceDialog cancelService = new CancelServiceDialog();
                    cancelService.setData(appointment, professional, activity);
                    cancelService.show(getSupportFragmentManager(), "dialog");
                }

                @Override
                public void onDialogNegativeClick(DialogFragment dialog) {

                }
            });
        } else {
            CancelServiceDialog cancelService = new CancelServiceDialog();
            cancelService.setData(appointment, professional, activity);
            cancelService.show(getSupportFragmentManager(), "dialog");
        }
    }

    @OnClick(R.id.rlBackButton)
    public void onBack() {
        onBackPressed();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onModelChange(BusEvents.UIUpdate event) {
        if ("buttonHistoricalAppointmentCancel".equals(event.ui)) {
            btnCancel.setVisibility(View.GONE);
            new ManageGeneralRequest(activity).downloadAll();
        }
        if ("buttonHistoricalAppointmentReview".equals(event.ui)) {
            btnReview.setVisibility(View.GONE);
            new ManageGeneralRequest(activity).downloadAll();
        }
    }
}
