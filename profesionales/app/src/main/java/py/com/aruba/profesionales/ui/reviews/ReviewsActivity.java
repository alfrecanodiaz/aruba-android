package py.com.aruba.profesionales.ui.reviews;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import jp.wasabeef.recyclerview.adapters.SlideInRightAnimationAdapter;
import py.com.aruba.profesionales.R;
import py.com.aruba.profesionales.data.models.Review;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.ui.reviews.recyclerReviews.ReviewsViewAdapter;
import py.com.aruba.profesionales.utils.Utils;
import py.com.aruba.profesionales.utils.UtilsImage;

public class ReviewsActivity extends AppCompatActivity {

    @BindView(R.id.ivProfile)
    ImageView ivProfile;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.llContentStar)
    LinearLayout llContentStar;
    @BindView(R.id.viewEmpty)
    LinearLayout viewEmpty;

    private AppCompatActivity activity;
    private Realm realm;
    private List<Review> listReview;
    private ReviewsViewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_reviews);

        // PART1 - All activities must have this settings
        ButterKnife.bind(this);
        activity = this;
        realm = GlobalRealm.getDefault();
        UtilsImage.loadImageCircle(activity, ivProfile, Utils.getAvatar());
        initRecycler();
        // END PART1

        // Obtenemos el average del review
        double avg = Review.get(realm).findAll().average("calification");
        setStar((avg > 0) ? (int) avg : 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Método para inicializar el recycler
     */
    private void initRecycler() {
        recyclerView.setVisibility(View.VISIBLE);

        // Obtenemos todos los sub-servicios asociados a la categoría
        RealmResults<Review> listReviewRealm = realm.where(Review.class).findAll();
        listReview = realm.copyFromRealm(listReviewRealm);
        Utils.checkEmptyStatus(listReview.size(), viewEmpty);

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


    @OnClick(R.id.rlBackButton)
    public void closeActivity(View view) {
        onBackPressed();
    }

    /**
     * Seteamos la cantidad de estrellas del review
     */
    private void setStar(int number) {
        llContentStar.removeAllViews();
        for (int i = 0; i < 5; i++) {
            final View view = LayoutInflater.from(ReviewsActivity.this).inflate(R.layout.icon_star_simple, llContentStar, false);

            ImageView star = view.findViewById(R.id.ivStar);
            if (i < number) star.setImageResource(R.drawable.star_white);

            llContentStar.addView(view);
        }
    }
}
