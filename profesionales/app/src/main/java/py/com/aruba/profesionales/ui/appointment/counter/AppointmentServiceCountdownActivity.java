package py.com.aruba.profesionales.ui.appointment.counter;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import py.com.aruba.profesionales.R;
import py.com.aruba.profesionales.data.eventbus.BusEvents;
import py.com.aruba.profesionales.data.eventbus.GlobalBus;
import py.com.aruba.profesionales.data.helpers.RestAdapter;
import py.com.aruba.profesionales.data.helpers.request.ManageGeneralRequest;
import py.com.aruba.profesionales.data.helpers.request.RequestResponseDataJsonObject;
import py.com.aruba.profesionales.data.models.Appointment;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.data.service.AppointmentInterface;
import py.com.aruba.profesionales.utils.Constants;
import py.com.aruba.profesionales.utils.TimeUtils;
import py.com.aruba.profesionales.utils.alerts.AlertGlobal;

public class AppointmentServiceCountdownActivity extends AppCompatActivity {

    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 547;
    @BindView(R.id.ivBgCircle)
    ImageView ivBgCircle;
    @BindView(R.id.ivBackground)
    ImageView ivBackground;
    @BindView(R.id.tvCounterDown)
    TextView tvCounterDown;

    private AppCompatActivity activity;
    private WindowManager windowManager;
    private View mRootView;
    private WindowManager.LayoutParams params;
    private boolean isFloatMode;
    private int appointment_id;
    Appointment appointment;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Si es mayor a M y podemos superponer pantallas
//        isFloatMode = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this));
        isFloatMode = false; // En algunas versiones de android, no deja ejecutar la ventana flotante, así que forzamos al usuario a que use la ventana completa
        activity = this;
        appointment_id = getIntent().getIntExtra("ID", 0);


        if (isFloatMode) {
            initFloatWindow();
            setData();
            finish();
        } else {
            setContentView(R.layout.activity_appointment_service_couter);
            // Si no estamos en float mode, preguntamos al usuario si quiere usar ese modo
//            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
//            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
            setData();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            if (resultCode == RESULT_OK) {
                initFloatWindow();
                setData();
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @OnClick({R.id.btnFinished, R.id.ivClose})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnFinished:
                setAppointmentCompleted();
                break;
            case R.id.ivClose:
                if (isFloatMode) {
                    if (mRootView != null) windowManager.removeView(mRootView);
                } else {
                    //activity.moveTaskToBack(true);
                    activity.finish();
                    System.exit(0);
                }
                break;
        }
    }


    /**
     * método para cambiar el appointment a "arrived"
     */
    private void setAppointmentCompleted() {
        // Preparamos el JSON para enviar la ubi
        JsonObject data = new JsonObject();
        data.addProperty("appointment_id", appointment.getBackend_id());

        // Enviamos al backend el appointment_id y la ubicación del usuario
        RestAdapter.getClient(activity).create(AppointmentInterface.class).set_completed(data).enqueue(new RequestResponseDataJsonObject("arrived") {
            @Override
            public void successful(JsonObject object) {
            }

            @Override
            public void successful(String msg) {
                realm.beginTransaction();
                appointment.setStatus(Constants.APPOINTMENT_STATUS.COMPLETED);
                realm.commitTransaction();
                AlertGlobal.showCustomSuccess(activity, "Excelente", msg);
                GlobalBus.getBus().post(new BusEvents.ModelUpdated("appointment"));

                activity.finish();
                System.exit(0);
            }

            @Override
            public void error(String msg) {
                AlertGlobal.showCustomError(activity, "Atención", msg);
            }
        });
    }

    /**
     * Inicializamos el view en modo flotante
     */
    private void initFloatWindow() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = layoutInflater.inflate(R.layout.activity_appointment_service_couter, null);
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0; // initial position
        params.y = 0; // initial position

        //this code is for dragging the chat head
        mRootView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int newX = initialX + (int) (event.getRawX() - initialTouchX); // new position
                        int newY = initialY + (int) (event.getRawY() - initialTouchY); // new position
                        params.x = newX;
                        params.y = newY;
                        windowManager.updateViewLayout(mRootView, params);
                        return true;
                }
                return false;
            }
        });

        windowManager.addView(mRootView, params);
    }

    /**
     * Seteamos los datos de la vista
     */
    private void setData() {

        // Contenido
        if (isFloatMode) {
            ButterKnife.bind(AppointmentServiceCountdownActivity.this, mRootView);
        } else {
            ButterKnife.bind(AppointmentServiceCountdownActivity.this);
        }

        // Obtenemos la instancia de realm
        realm = GlobalRealm.getDefault();

        // Animamos el counter
        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(3000);
        rotate.setRepeatCount(600);
        rotate.setInterpolator(new LinearInterpolator());
        ivBgCircle.startAnimation(rotate);

        // Obtenemos el appointment
        appointment = Appointment.get(realm).equalTo("backend_id", appointment_id).findFirst();

        // Counter
        start_countdown_timer();
    }

    private void start_countdown_timer() {
        long hour_start = appointment.getStart_at();
        long duration = appointment.getDuration();
        long current_hour = TimeUtils.getActualTimeInSeconds();

        // Tiempo restante del servicio
        long remaining = ((hour_start + duration) - current_hour) * 1000; // multiplicamos por 1000 prque se calcula en milisegundos


        new CountDownTimer(remaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String hoursLeft = String.format("%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished));
                String minutesLeft = String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished));
                String secondsLeft = String.format("%02d", (millisUntilFinished / 1000) % 60);

                tvCounterDown.setText(String.format("%s:%s:%s", hoursLeft, minutesLeft, secondsLeft));
            }

            @Override
            public void onFinish() {
                tvCounterDown.setText("Tiempo");
            }
        }.start();

    }

}
