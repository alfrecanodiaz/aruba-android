package py.com.aruba.clientes.ui.main.dialogs;

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
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.eventbus.BusEvents;
import py.com.aruba.clientes.data.eventbus.GlobalBus;
import py.com.aruba.clientes.data.helpers.RestAdapter;
import py.com.aruba.clientes.data.helpers.request.RequestResponseDataJsonObject;
import py.com.aruba.clientes.data.models.Professional;
import py.com.aruba.clientes.data.models.User;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.data.service.ProfessionalInterface;
import py.com.aruba.clientes.utils.Constants;
import py.com.aruba.clientes.utils.alerts.AlertGlobal;
import py.com.aruba.clientes.utils.dialogs.CustomDialog;
import py.com.aruba.clientes.utils.images.UtilsImage;
import py.com.aruba.clientes.utils.loading.UtilsLoading;

public class ReviewDialog extends CustomDialog {


    @BindView(R.id.tvClient) TextView tvClient;
    @BindView(R.id.tvProfessional) TextView tvProfessional;
    @BindView(R.id.ivAvatar) ImageView ivAvatar;
    @BindView(R.id.ratingBar) AppCompatRatingBar ratingBar;
    @BindView(R.id.etText) EditText etText;
    Realm realm;
    private Professional professional;
    private AppCompatActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_review_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    /**
     * Seteamos los datos que vamos a mostrar en el popup
     *
     * @param professional
     * @param activity
     */
    public void setData(Professional professional, AppCompatActivity activity) {
        this.professional = professional;
        this.activity = activity;
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

        UtilsImage.loadImageCircle(activity, ivAvatar, professional.getAvatar(), Constants.PLACEHOLDERS.AVATAR);
        tvClient.setText(String.format("¡Hola %s!", User.getUser(realm).getFirst_name()));
        tvProfessional.setText(professional.getFullName());
    }

    @OnClick({R.id.btnAcept, R.id.ivClose})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnAcept:
                sendToBackend();
                break;
            case R.id.ivClose:
                ReviewDialog.this.dismiss();
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
        data.addProperty("user_id", professional.getId());
        data.addProperty("rating_number", ratingBar.getRating());
        data.addProperty("text", etText.getText().toString());
        ProfessionalInterface restInterface = RestAdapter.getClient(activity).create(ProfessionalInterface.class);

        restInterface.review_professional(data).enqueue(new RequestResponseDataJsonObject("review_pro") {
            @Override
            public void successful(JsonObject object) {
                utilsLoading.dismissLoading();
                ReviewDialog.this.dismiss();
                AlertGlobal.showCustomSuccess(activity, "Atención", "Calificado correctamente");
            }
            @Override
            public void successful(String msg) {
                utilsLoading.dismissLoading();
                ReviewDialog.this.dismiss();
                AlertGlobal.showCustomSuccess(activity, "Atención", msg);
                GlobalBus.getBus().post(new BusEvents.UIUpdate("buttonHistoricalAppointmentReview"));
            }
            @Override
            public void error(String msg) {
                utilsLoading.dismissLoading();
                ReviewDialog.this.dismiss();
                AlertGlobal.showCustomError(activity, "Atención", msg);
            }
        });
    }

}
