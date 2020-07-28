package py.com.aruba.profesionales.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import py.com.aruba.profesionales.R;
import py.com.aruba.profesionales.data.eventbus.BusEvents;
import py.com.aruba.profesionales.data.eventbus.GlobalBus;
import py.com.aruba.profesionales.data.helpers.RestAdapter;
import py.com.aruba.profesionales.data.helpers.request.ManageGeneralRequest;
import py.com.aruba.profesionales.data.models.Appointment;
import py.com.aruba.profesionales.data.models.Review;
import py.com.aruba.profesionales.data.models.User;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.ui.appointment.AppointmentsActivity;
import py.com.aruba.profesionales.ui.auth.SelectLoginActivity;
import py.com.aruba.profesionales.ui.availability.AvailabilityActivity;
import py.com.aruba.profesionales.ui.balance.BalanceActivity;
import py.com.aruba.profesionales.ui.profile.goals.GoalsActivity;
import py.com.aruba.profesionales.ui.profile.ProfileActivity;
import py.com.aruba.profesionales.ui.ranking.RankingListActivity;
import py.com.aruba.profesionales.ui.reviews.ReviewsActivity;
import py.com.aruba.profesionales.ui.reviews.dialogs.ReviewDialog;
import py.com.aruba.profesionales.ui.reviews.reviewClient.DialogReviewClient;
import py.com.aruba.profesionales.utils.Print;
import py.com.aruba.profesionales.utils.SharedPreferencesUtils;
import py.com.aruba.profesionales.utils.Utils;
import py.com.aruba.profesionales.utils.UtilsImage;
import py.com.aruba.profesionales.utils.dialogs.CustomDialog;
import py.com.aruba.profesionales.utils.dialogs.ErrorDialog;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.ivProfile) ImageView ivProfile;
    @BindView(R.id.iconSchedule) ImageView iconSchedule;
    @BindView(R.id.iconScores) ImageView iconScores;
    @BindView(R.id.iconGoals) ImageView iconGoals;
    @BindView(R.id.iconBalance) ImageView iconBalance;
    @BindView(R.id.iconAvailability) ImageView iconAvailability;
    @BindView(R.id.tvSchedule) TextView tvSchedule;
    @BindView(R.id.tvScores) TextView tvScores;
    @BindView(R.id.tvGoals) TextView tvGoals;
    @BindView(R.id.tvBalance) TextView tvBalance;
    @BindView(R.id.tvAvailability) TextView tvAvailability;
    @BindView(R.id.etGreeting) TextView etGreeting;
    @BindView(R.id.etFirstName) TextView etFirstName;

    private Context context;
    AppCompatActivity activity;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_main);

        // PART1 - All activities must have this settings
        ButterKnife.bind(this);
        context = this;
        activity = this;
        realm = GlobalRealm.getDefault();
        GlobalBus.getBus().register(this);
        // END PART1

        setDataContext();
        new ManageGeneralRequest(this).downloadAll();
        UtilsImage.setCurrentDate(context);
//        showReviewPopup();
    }

    /**
     * Método para mostrar el popup para calificar
     */
    private void showReviewPopup() {
        Realm realm = GlobalRealm.getDefault();
        Appointment appointment = realm.where(Appointment.class).findFirst();
        DialogReviewClient rd = new DialogReviewClient();
        rd.setData(activity, appointment);
        rd.show((getSupportFragmentManager()), "dialog");
    }

    /**
     * Método para setear lo datos relacionados al usuario
     */
    private void setDataContext() {
        User user = realm.where(User.class).findFirst();
        etFirstName.setText(user.getFirst_name());
        etGreeting.setText(getGreetingText());
        UtilsImage.loadImageCircle(context, ivProfile, user.getAvatar());
    }

    /**
     * Método para obtener el saludo de la pantalla principal, de acuerdo al día
     *
     * @return
     */
    private String getGreetingText() {
        Calendar rightNow = Calendar.getInstance();
        int currentHour = rightNow.get(Calendar.HOUR_OF_DAY) + 1; // Range 0:23

        if (currentHour < 12) {
            return "¡BUENOS DÍAS!";
        } else if (currentHour < 18) {
            return "¡BUENAS TARDES!";
        } else {
            return "¡BUENAS NOCHES!";
        }

    }

    @OnClick({R.id.btnProfile, R.id.llSchedule, R.id.llScores, R.id.llRanking, R.id.llBalance, R.id.llAvailability, R.id.llClose})
    public void onMenuClicked(View view) {
        Utils.preventTwoClick(view);
        Pair<View, String> pProfile = Pair.create((View) ivProfile, "profile");
        Pair<View, String> pIcon;
//        Pair<View, String> pTitle;
        Intent intent;
        ActivityOptionsCompat options;

        switch (view.getId()) {
            case R.id.btnProfile:
                startActivity(new Intent(context, ProfileActivity.class));
                break;
            case R.id.llSchedule:
                intent = new Intent(this, AppointmentsActivity.class);
                pIcon = Pair.create((View) iconSchedule, "icon");
//                pTitle = Pair.create((View) tvSchedule, "title");

                options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pProfile, pIcon);
                startActivity(intent, options.toBundle());
                break;
            case R.id.llScores:
                intent = new Intent(this, ReviewsActivity.class);
                pIcon = Pair.create((View) iconScores, "icon");
//                pTitle = Pair.create((View) tvScores, "title");

                options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pProfile, pIcon);
                startActivity(intent, options.toBundle());
                break;
            case R.id.llRanking:
                intent = new Intent(this, RankingListActivity.class);
                pIcon = Pair.create((View) iconGoals, "icon");
//                pTitle = Pair.create((View) tvGoals, "title");

                options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pProfile, pIcon);
                startActivity(intent, options.toBundle());
                break;
            case R.id.llBalance:
                intent = new Intent(this, BalanceActivity.class);
                pIcon = Pair.create((View) iconBalance, "icon");
//                pTitle = Pair.create((View) tvBalance, "title");

                options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pProfile, pIcon);
                startActivity(intent, options.toBundle());
                break;
            case R.id.llAvailability:
                intent = new Intent(this, AvailabilityActivity.class);
                pIcon = Pair.create((View) iconAvailability, "icon");
//                pTitle = Pair.create((View) tvAvailability, "title");

                options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pProfile, pIcon);
                startActivity(intent, options.toBundle());
                break;
            case R.id.llClose:
                ErrorDialog customDialog = new ErrorDialog();
                customDialog.setData("Atención", "¿Está seguro que quiere cerrar sesión?");
                customDialog.setListener(new CustomDialog.ButtonsListener() {
                    @Override
                    public void onDialogPositiveClick(DialogFragment dialog) {
                        Print.e("dialogo", "positivo");

                        // Limpiamos SharedPreferences
                        SharedPreferencesUtils.clearSharedPreference(context);
                        RestAdapter.setNull();

                        // Limpiamos la base de datos, los datos asociados a un usuario
                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.deleteAll();

                                // Mostramos la pantalla de iniciar sesión
                                Intent iniciarSesion = new Intent(context, SelectLoginActivity.class);
                                iniciarSesion.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                context.startActivity(iniciarSesion);
                            }
                        });

                    }

                    @Override
                    public void onDialogNegativeClick(DialogFragment dialog) {
                        Print.e("dialogo", "negativo");
                    }
                });
                customDialog.show(getSupportFragmentManager(), "dialog");
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onModelChange(BusEvents.ModelUpdated event) {
        if ("profile".equals(event.model)) {
            setDataContext();
        }
    }
}
