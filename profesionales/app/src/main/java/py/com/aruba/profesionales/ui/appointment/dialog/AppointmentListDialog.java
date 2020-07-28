package py.com.aruba.profesionales.ui.appointment.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import py.com.aruba.profesionales.R;
import py.com.aruba.profesionales.data.models.Appointment;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.ui.appointment.AppointmentDetailsActivity;
import py.com.aruba.profesionales.utils.Constants;
import py.com.aruba.profesionales.utils.Utils;
import py.com.aruba.profesionales.utils.dialogs.CustomDialog;

public class AppointmentListDialog extends CustomDialog {


    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.llContentAppointments) LinearLayout llContentAppointments;


    private Realm realm;
    private AppCompatActivity activity;
    private RealmResults<Appointment> listAppointments;
    private Calendar calendar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_show_appointments, container, false);
        ButterKnife.bind(this, view);
        realm = GlobalRealm.getDefault();
        return view;
    }


    public void setData(AppCompatActivity activity, RealmResults<Appointment> listAppointments, Calendar calendar) {
        this.activity = activity;
        this.listAppointments = listAppointments;
        this.calendar = calendar;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTitle.setText(String.format("%s/%s/%s", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR)));
        setAllappointments();
    }

    /**
     * Preparamos las
     */
    private void setAllappointments() {
        llContentAppointments.removeAllViews();
        for (Appointment appointment : listAppointments) {
            final View view = LayoutInflater.from(activity).inflate(R.layout.item_appointment_list, llContentAppointments, false);
            TextView tvClient = view.findViewById(R.id.tvClient);
            TextView tvTime = view.findViewById(R.id.tvTime);
            RelativeLayout rlItem = view.findViewById(R.id.rlItem);
            View ivViewBorder = view.findViewById(R.id.ivViewBorder);

            tvClient.setText(appointment.getClient());
            tvTime.setText(appointment.getHour_start_pretty());

            // Color del borde del appointment
            if (appointment.getStatus() == Constants.APPOINTMENT_STATUS.CANCELED) {
                ivViewBorder.setBackgroundColor(getResources().getColor(R.color.brown2));
            } else if (appointment.getStatus() == Constants.APPOINTMENT_STATUS.COMPLETED) {
                ivViewBorder.setBackgroundColor(getResources().getColor(R.color.aquamarineBackground));
            } else {
                ivViewBorder.setBackgroundColor(getResources().getColor(R.color.brown1));
            }

            // Evento onclick del item
            rlItem.setOnClickListener(v -> {
                Utils.preventTwoClick(v);

                Intent intent = new Intent(activity, AppointmentDetailsActivity.class);
                intent.putExtra("ID", appointment.getBackend_id());
                startActivity(intent);
            });

            // Agregamos la vista al contenedor padre
            llContentAppointments.addView(view);
        }
    }

    @OnClick({R.id.ivClose})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivClose:
                AppointmentListDialog.this.dismiss();
                break;
        }
    }

}
