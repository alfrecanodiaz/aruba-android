package py.com.aruba.clientes.ui.professionals;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

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
import py.com.aruba.clientes.data.helpers.RestAdapter;
import py.com.aruba.clientes.data.helpers.request.RequestResponseDataJsonObject;
import py.com.aruba.clientes.data.models.Professional;
import py.com.aruba.clientes.data.models.Review;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.data.service.ProfessionalInterface;
import py.com.aruba.clientes.ui.professionals.recyclerReviews.ReviewsViewAdapter;
import py.com.aruba.clientes.utils.Constants;
import py.com.aruba.clientes.utils.Utils;
import py.com.aruba.clientes.utils.alerts.AlertGlobal;
import py.com.aruba.clientes.utils.images.UtilsImage;
import py.com.aruba.clientes.utils.loading.UtilsLoading;

public class ProfessionalDetailActivity extends AppCompatActivity {

    @BindView(R.id.tvProfessionalName)
    TextView tvProfessionalName;
    @BindView(R.id.ivAvatar)
    ImageView ivAvatar;
    @BindView(R.id.ivLike)
    ImageView ivLike;
    @BindView(R.id.tvLikes)
    TextView tvLikes;
    @BindView(R.id.tvServices)
    TextView tvServices;
    @BindView(R.id.tvComments)
    TextView tvComments;
    @BindView(R.id.tvAverage)
    TextView tvAverage;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private Realm realm;
    private AppCompatActivity activity;
    private Professional professional;
    private List<Review> listReview;
    private ReviewsViewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professional_detail);

        // PART1 - All activities must have this settings
        ButterKnife.bind(this);
        realm = GlobalRealm.getDefault();
        activity = this;
        GlobalBus.getBus().register(this);
        // END

        int professional_id = getIntent().getIntExtra("ID", 1);
        professional = realm.where(Professional.class).equalTo("id", professional_id).findFirst();

        setProfessionalData();
        initRecycler();
    }

    /**
     * Método para setear los datos del profesional
     */
    private void setProfessionalData() {
        tvProfessionalName.setText(professional.getFullName());
        tvLikes.setText(String.valueOf(professional.getLikes_count()));
        tvServices.setText(String.valueOf(professional.getServices_count()));
        tvComments.setText(String.valueOf(professional.getReviews_count()));
        tvAverage.setText(String.valueOf(professional.getAverage_reviews()));
        UtilsImage.loadImageCircle(activity, ivAvatar, professional.getAvatar(), Constants.PLACEHOLDERS.AVATAR);

        int heart = (professional.isLiked()) ? R.drawable.like : R.drawable.unlike;
        ivLike.setImageResource(heart);
        // Método onclick del like
        ivLike.setOnClickListener(v -> {
            Utils.preventTwoClick(v);
            UtilsLoading ul = new UtilsLoading(activity);
            ul.showLoading();

            JsonObject data = new JsonObject();
            data.addProperty("professional_id",  professional.getId());

            ProfessionalInterface restInterface = RestAdapter.getClient(activity).create(ProfessionalInterface.class);
            restInterface.set_like(data).enqueue(new RequestResponseDataJsonObject("like") {
                @Override
                public void successful(JsonObject object) {

                }

                @Override
                public void successful(String msg) {
                    ul.dismissLoading();
                    AlertGlobal.showCustomSuccess(activity, "Excelente", msg);

                    boolean liked = professional.isLiked();
                    int like_count = professional.getLikes_count();

                    // Si esta likeado, restamos, de lo contrario sumamos
                    if (liked && like_count > 0) {
                        tvLikes.setText(String.valueOf(--like_count));
                        ivLike.setImageResource(R.drawable.unlike);
                    } else {
                        tvLikes.setText(String.valueOf(++like_count));
                        ivLike.setImageResource(R.drawable.like);
                    }

                    // Actualizamos el objeto de la BD
                    realm.beginTransaction();
                    professional.setLiked(!liked);
                    professional.setLikes_count(like_count);
                    realm.copyToRealmOrUpdate(professional);
                    realm.commitTransaction();

                    GlobalBus.getBus().post(new BusEvents.UIUpdate("professional"));
                }

                @Override
                public void error(String msg) {
                    ul.dismissLoading();
                    AlertGlobal.showCustomError(activity, "Atención", msg);
                }
            });


        });


    }


    /**
     * Método para inicializar el recycler
     */
    private void initRecycler() {
        recyclerView.setVisibility(View.VISIBLE);

        // Obtenemos todos los sub-servicios asociados a la categoría
        RealmResults<Review> listReviewRealm = realm.where(Review.class).equalTo("professional_id", professional.getId()).findAll();
        listReview = realm.copyFromRealm(listReviewRealm);

        // Recycler View
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(mLayoutManager);
        reviewAdapter = new ReviewsViewAdapter(activity, listReview, activity);
        recyclerView.setAdapter(reviewAdapter);

        // Custom animation
        recyclerView.setAdapter(new SlideInRightAnimationAdapter(reviewAdapter));
    }

    /**
     * Método para actualizar el recyclerview, luego de filtrarse
     */
    private void updateRecycler() {
        reviewAdapter.update(listReview);
    }

    @OnClick({R.id.rlBackButton})
    public void onViewClicked(View view) {
        Utils.preventTwoClick(view);
        switch (view.getId()) {
            case R.id.rlBackButton:
                onBackPressed();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUIChange(BusEvents.UIUpdate event) {
        if(event.ui.equals("professional"))
            setProfessionalData();
    }
}
