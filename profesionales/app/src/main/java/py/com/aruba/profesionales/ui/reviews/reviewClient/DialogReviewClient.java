package py.com.aruba.profesionales.ui.reviews.reviewClient;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;

import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import py.com.aruba.profesionales.R;
import py.com.aruba.profesionales.data.eventbus.BusEvents;
import py.com.aruba.profesionales.data.eventbus.GlobalBus;
import py.com.aruba.profesionales.data.helpers.RestAdapter;
import py.com.aruba.profesionales.data.helpers.request.RequestResponseDataJsonObject;
import py.com.aruba.profesionales.data.models.Appointment;
import py.com.aruba.profesionales.data.models.Professional;
import py.com.aruba.profesionales.data.models.Review;
import py.com.aruba.profesionales.data.models.User;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.data.service.ReviewsInterface;
import py.com.aruba.profesionales.ui.main.MainActivity;
import py.com.aruba.profesionales.ui.reviews.dialogs.ReviewDialog;
import py.com.aruba.profesionales.utils.Constants;
import py.com.aruba.profesionales.utils.UtilsImage;
import py.com.aruba.profesionales.utils.alerts.AlertGlobal;
import py.com.aruba.profesionales.utils.dialogs.CustomDialog;
import py.com.aruba.profesionales.utils.loading.UtilsLoading;

public class DialogReviewClient extends CustomDialog {


    @BindView(R.id.tvClient) TextView tvClient;
    @BindView(R.id.tvProfessional) TextView tvProfessional;
    @BindView(R.id.ivAvatar) ImageView ivAvatar;
    @BindView(R.id.ratingBar) AppCompatRatingBar ratingBar;
    @BindView(R.id.etText) EditText etText;
    Realm realm;
    private AppCompatActivity activity;
    private Appointment appointment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_review_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    /**
     * Seteamos los datos que vamos a mostrar en el popup
     *
     * @param appointment
     * @param activity
     */
    public void setData(AppCompatActivity activity, Appointment appointment) {
        this.activity = activity;
        this.appointment = appointment;
    }

    /**
     * The system calls this only when creating the layout in a dialog.
     */
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
        realm = GlobalRealm.getDefault();

        UtilsImage.loadImageCircle(activity, ivAvatar, appointment.getClient_avatar(), R.drawable.avatar);
        tvClient.setText(String.format("¡Hola %s!", User.getUser().getFirst_name()));
        tvProfessional.setText(appointment.getClient());
    }

    @OnClick({R.id.btnAcept, R.id.ivClose})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnAcept:
                sendToBackend();
                break;
            case R.id.ivClose:
                DialogReviewClient.this.dismiss();
                break;
        }
    }

    /**
     * Método para enviar al backend la calificación del profesional
     */
    private void sendToBackend() {
        UtilsLoading utilsLoading = new UtilsLoading(activity);
        utilsLoading.showLoading();

        JsonObject data = new JsonObject();
        data.addProperty("user_id", appointment.getProfessional_id());
        data.addProperty("rating_number", ratingBar.getRating());
        data.addProperty("text", etText.getText().toString());
        ReviewsInterface restInterface = RestAdapter.getClient(activity).create(ReviewsInterface.class);

        restInterface.create(data).enqueue(new RequestResponseDataJsonObject("review_pro") {
            @Override
            public void successful(JsonObject object) {
                utilsLoading.dismissLoading();
                DialogReviewClient.this.dismiss();
                AlertGlobal.showCustomSuccess(activity, "Atención", "Calificado correctamente");
            }
            @Override
            public void successful(String msg) {
                utilsLoading.dismissLoading();
                DialogReviewClient.this.dismiss();
                AlertGlobal.showCustomSuccess(activity, "Atención", msg);
            }
            @Override
            public void error(String msg) {
                utilsLoading.dismissLoading();
                DialogReviewClient.this.dismiss();
                AlertGlobal.showCustomError(activity, "Atención", msg);
            }
        });
    }


}
