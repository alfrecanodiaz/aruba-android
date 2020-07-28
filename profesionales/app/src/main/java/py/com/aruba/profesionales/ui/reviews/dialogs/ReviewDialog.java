package py.com.aruba.profesionales.ui.reviews.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import py.com.aruba.profesionales.R;
import py.com.aruba.profesionales.data.models.Appointment;
import py.com.aruba.profesionales.data.models.Review;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.ui.reviews.reviewClient.DialogReviewClient;
import py.com.aruba.profesionales.utils.UtilsImage;
import py.com.aruba.profesionales.utils.dialogs.CustomDialog;

public class ReviewDialog extends CustomDialog {


    @BindView(R.id.tvComment) TextView tvComment;
    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.ivAvatar) ImageView ivAvatar;
    @BindView(R.id.ratingBar) RatingBar ratingBar;

    private Realm realm;
    private AppCompatActivity activity;
    private Review review;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_review_comment, container, false);
        ButterKnife.bind(this, view);
        realm = GlobalRealm.getDefault();
        return view;
    }

    /**
     * Seteamos los datos que vamos a mostrar en el popup
     *
     * @param activity
     */
    public void setData(AppCompatActivity activity, Review review) {
        this.activity = activity;
        this.review = review;
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

        tvTitle.setText(String.format("%s te ha calificado", review.getUsername()));
        tvComment.setText(review.getComment());
        ratingBar.setRating(review.getCalification());
        UtilsImage.loadImageCircle(activity, ivAvatar, review.getClient_avatar());
    }

    @OnClick({R.id.btnRate, R.id.ivClose})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnRate:
                ReviewDialog.this.dismiss();

                Appointment appointment = realm.where(Appointment.class).findFirst();

                if(appointment != null){
                    DialogReviewClient rd = new DialogReviewClient();
                    rd.setData(activity, appointment);
                    rd.show((activity.getSupportFragmentManager()), "dialog");
                }
                break;
            case R.id.ivClose:
                ReviewDialog.this.dismiss();
                break;
        }
    }
}
