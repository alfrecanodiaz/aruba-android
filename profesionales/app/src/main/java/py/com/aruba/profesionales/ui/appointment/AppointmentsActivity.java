package py.com.aruba.profesionales.ui.appointment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import py.com.aruba.profesionales.R;
import py.com.aruba.profesionales.data.eventbus.BusEvents;
import py.com.aruba.profesionales.data.eventbus.GlobalBus;
import py.com.aruba.profesionales.data.models.Appointment;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.ui.appointment.calendarView.EventDecorator;
import py.com.aruba.profesionales.ui.appointment.dialog.AppointmentListDialog;
import py.com.aruba.profesionales.utils.Constants;
import py.com.aruba.profesionales.utils.Utils;
import py.com.aruba.profesionales.utils.UtilsImage;
import py.com.aruba.profesionales.utils.alerts.AlertGlobal;

public class AppointmentsActivity extends AppCompatActivity {

    @BindView(R.id.ivProfile) ImageView ivProfile;
    @BindView(R.id.calendarView) MaterialCalendarView widget;
    private Context context;
    private Realm realm;
    private AppCompatActivity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_schedule);

        // PART1 - All activities must have this settings
        ButterKnife.bind(this);
        context = this;
        realm = GlobalRealm.getDefault();
        activity = AppointmentsActivity.this;
        UtilsImage.loadImageCircle(context, ivProfile, Utils.getAvatar());
        GlobalBus.getBus().register(this);
        // END PART1

        initCalendar();
    }

    private void initCalendar() {
        setEvents();
    }


    private void setEvents() {


        // Obtememos todos los appointments del profesional
        RealmResults<Appointment> listAppointments = Appointment.get(realm).findAll();

        for (Appointment appointment : listAppointments) {
            List<CalendarDay> calendarDays = new ArrayList<>();

            Date d = appointment.getDate();
            Calendar c = Calendar.getInstance();
            c.setTime(d);

            CalendarDay formatted_date = CalendarDay.from(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
            calendarDays.add(formatted_date);


            // Agregamos el evento
            if (appointment.getStatus() == Constants.APPOINTMENT_STATUS.CANCELED) {
                widget.addDecorator(new EventDecorator(getResources().getColor(R.color.brown2), calendarDays));
            } else if (appointment.getStatus() == Constants.APPOINTMENT_STATUS.COMPLETED) {
                widget.addDecorator(new EventDecorator(getResources().getColor(R.color.aquamarineBackground), calendarDays));
            } else {
                widget.addDecorator(new EventDecorator(getResources().getColor(R.color.brown1), calendarDays));
            }
        }


        // Onclick en una fecha del calendario
        widget.setOnDateChangedListener((widget, date, selected) -> {
            Utils.preventTwoClick(widget);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, date.getDay()); // make sure month stays valid
            calendar.set(Calendar.YEAR, date.getYear());
            calendar.set(Calendar.MONTH, date.getMonth() - 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            RealmResults<Appointment> listAppoint = getAppointmentsByDate(calendar);

            if (listAppoint.size() > 0) {
                AppointmentListDialog appointmentDialog = new AppointmentListDialog();
                appointmentDialog.setData(activity, listAppoint, calendar);
                appointmentDialog.show(getSupportFragmentManager(), "dialog");
            } else {
                AlertGlobal.showCustomError(activity, "Atención", "No existen reservas para la fecha seleccionada");
            }
        });

    }


    /**
     * Método para retornarn los appointments de una fecha X
     *
     * @param cal
     * @return
     */
    private RealmResults<Appointment> getAppointmentsByDate(Calendar cal) {
        return realm.where(Appointment.class).equalTo("date", cal.getTime()).findAll();
    }

    @OnClick(R.id.rlBackButton)
    public void closeActivity(View view) {
        onBackPressed();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onModelChange(BusEvents.ModelUpdated event) {
        if ("appointment".equals(event.model)) {
            initCalendar();
        }
    }

}
