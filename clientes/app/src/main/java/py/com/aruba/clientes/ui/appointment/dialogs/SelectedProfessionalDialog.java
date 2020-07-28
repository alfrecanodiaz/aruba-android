package py.com.aruba.clientes.ui.appointment.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.eventbus.BusEvents;
import py.com.aruba.clientes.data.eventbus.GlobalBus;
import py.com.aruba.clientes.data.models.Appointment;
import py.com.aruba.clientes.data.models.Professional;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.ui.appointment.AppointmentReservationActivity;
import py.com.aruba.clientes.ui.professionals.ProfessionalDetailActivity;
import py.com.aruba.clientes.utils.Constants;
import py.com.aruba.clientes.utils.TimeUtils;
import py.com.aruba.clientes.utils.dialogs.CustomDialog;
import py.com.aruba.clientes.utils.images.UtilsImage;

public class SelectedProfessionalDialog extends CustomDialog {

    @BindView(R.id.ivAvatar)
    ImageView ivAvatar;
    @BindView(R.id.tvProfessionalName)
    TextView tvProfessionalName;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.tvHour)
    TextView tvHour;
    @BindView(R.id.tvLikes)
    TextView tvLikes;
    @BindView(R.id.tvServices)
    TextView tvServices;
    @BindView(R.id.tvComments)
    TextView tvComments;
    @BindView(R.id.tvAverage)
    TextView tvAverage;
    @BindView(R.id.llContentDetails)
    LinearLayout llContentDetails;


    private int professionalID;
    private Realm realm;
    private Context context;
    private Integer scheduleId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_professional_selected, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    /**
     * Seteamos los datos que vamos a mostrar en el popup
     *
     * @param id
     * @param scheduleId
     */
    public void setData(int id, Integer scheduleId, Context context) {
        this.professionalID = id;
        this.scheduleId = scheduleId;
        this.context = context;
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
        Professional professional = realm.where(Professional.class).equalTo("id", professionalID).findFirst();
        Appointment appointment = realm.where(Appointment.class).equalTo("id", scheduleId).findFirst();

        tvProfessionalName.setText(String.format("%s", professional.getFullName()));
        UtilsImage.loadImageCircle(context, ivAvatar, professional.getAvatar(), Constants.PLACEHOLDERS.AVATAR);
        tvDate.setText(TimeUtils.getDateStringReadable(appointment.getDate()));
        tvHour.setText(appointment.getHour_start_pretty());

        // Seteamos los datos del profesional
        tvLikes.setText(String.valueOf(professional.getLikes_count()));
        tvServices.setText(String.valueOf(professional.getServices_count()));
        tvComments.setText(String.valueOf(professional.getReviews_count()));
        tvAverage.setText(String.valueOf(professional.getAverage_reviews()));
    }

    @OnClick({R.id.btnReject, R.id.btnContinue, R.id.ivAvatar})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnReject:
                SelectedProfessionalDialog.this.dismiss();
                break;
            case R.id.btnContinue:
                GlobalBus.getBus().post(new BusEvents.SchedulePage(2));
                SelectedProfessionalDialog.this.dismiss();
                break;
            case R.id.ivAvatar:
                Intent intent = new Intent(context, ProfessionalDetailActivity.class);
                intent.putExtra("ID", professionalID);
                // Shared transition
                Pair<View, String> p1 = Pair.create(tvProfessionalName, "tvProfessionalName");
                Pair<View, String> p2 = Pair.create(ivAvatar, "ivAvatar");
                Pair<View, String> p3 = Pair.create(llContentDetails, "llContentDetails");


                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((AppointmentReservationActivity) context, p1, p2, p3);
                context.startActivity(intent, options.toBundle());
                break;
        }
    }
}