package py.com.aruba.profesionales.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import py.com.aruba.profesionales.R;
import py.com.aruba.profesionales.data.eventbus.BusEvents;
import py.com.aruba.profesionales.data.eventbus.GlobalBus;
import py.com.aruba.profesionales.data.helpers.RestAdapter;
import py.com.aruba.profesionales.data.helpers.request.ManageGeneralRequest;
import py.com.aruba.profesionales.data.helpers.request.RequestResponseDataJsonObject;
import py.com.aruba.profesionales.data.models.Category;
import py.com.aruba.profesionales.data.models.Review;
import py.com.aruba.profesionales.data.models.User;
import py.com.aruba.profesionales.data.models.Zone;
import py.com.aruba.profesionales.data.realm.GlobalRealm;
import py.com.aruba.profesionales.data.service.ProfileInterface;
import py.com.aruba.profesionales.ui.profile.services.CategoriesActivity;
import py.com.aruba.profesionales.ui.profile.workzone.MapZoneActivity;
import py.com.aruba.profesionales.utils.UtilsImage;
import py.com.aruba.profesionales.utils.alerts.AlertGlobal;
import py.com.aruba.profesionales.utils.loading.UtilsLoading;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.etFirstName) EditText etFirstName;
    @BindView(R.id.etLastName) EditText etLastName;
    @BindView(R.id.etBirthDate) EditText etBirthDate;
    @BindView(R.id.etDocument) EditText etDocument;
    @BindView(R.id.etEmail) EditText etEmail;
    @BindView(R.id.etWorkZone) TextView etWorkZone;
    @BindView(R.id.ivAvatar) ImageView ivAvatar;
    @BindView(R.id.ratingBar) RatingBar ratingBar;
    @BindView(R.id.llContentCategories) LinearLayout llContentCategories;
    private Realm realm;
    private Uri resultUri = null;
    private AppCompatActivity activity;
    private UtilsLoading loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // PART1 - All activities must have this settings
        ButterKnife.bind(this);
        activity = this;
        realm = GlobalRealm.getDefault();
        GlobalBus.getBus().register(this);
        // END PART1

        setDataContext();
    }

    @OnClick(R.id.rlBackButton)
    public void closeActivity(View view) {
        onBackPressed();
    }

    /**
     * Método para setear lo datos relacionados al usuario
     */
    private void setDataContext() {
        User user = User.getUser();
        if (user == null) return;

        etFirstName.setText(user.getFirst_name());
        etLastName.setText(user.getLast_name());
        etEmail.setText(user.getEmail());
        etBirthDate.setText(user.getBirthday());
        etDocument.setText(user.getDocument());
        UtilsImage.loadImageCircle(activity, ivAvatar, user.getAvatar());

        // Zone
        Zone zone = Zone.get(realm).findFirst();
        if (zone != null) etWorkZone.setText("Zona guardada");

        // Categories
        setCategoriesIcons();

        double avg = Review.get(realm).findAll().average("calification");
        ratingBar.setRating((avg > 0) ? (int) avg : 0);
    }

    /**
     * metodo para setear las categorias activas del profesional
     */
    private void setCategoriesIcons() {
        RealmResults<Category> listCategories = Category.get(realm).equalTo("enabled", true).findAll();
        llContentCategories.removeAllViews();
        for (Category c : listCategories) {
            final View view = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.icon_categorie_simple, llContentCategories, false);
            ImageView categorie = view.findViewById(R.id.ivCategorie);
            UtilsImage.loadImage(activity, categorie, c.getImage_url());
            llContentCategories.addView(view);
        }
    }

    @OnClick({R.id.etWorkZone, R.id.ivServices, R.id.btnSave})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.etWorkZone:
                Intent workZoneIntent = new Intent(activity, MapZoneActivity.class);
                // Si no esta vacio agregamos al inten la bandera para editar
                workZoneIntent.putExtra("is_new", (etWorkZone.getText().toString().isEmpty()));
                startActivity(workZoneIntent);
                break;
            case R.id.ivServices:
                startActivity(new Intent(activity, CategoriesActivity.class));
                break;
            case R.id.btnSave:
                loading = new UtilsLoading(activity);
                loading.showLoading();

                if (resultUri != null) saveAvatar();
                saveProfile();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onModelChange(BusEvents.ModelUpdated event) {
        if ("profile".equals(event.model)) {
            setDataContext();
        }
    }

    @OnClick(R.id.ivEditAvatar)
    public void onViewClicked() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    UCrop.of(selectedImage, Uri.fromFile(new File(getCacheDir(), getFileName())))
                            .withAspectRatio(1, 1)
                            .withMaxResultSize(250, 250)
                            .start(ProfileActivity.this);
                }
                break;
            case UCrop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    resultUri = UCrop.getOutput(data);
                    Glide.with(activity)
                            .load(resultUri)
                            .centerCrop()
                            .placeholder(R.drawable.avatar)
                            .error(R.drawable.avatar)
                            .fallback(R.drawable.avatar)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .apply(RequestOptions.circleCropTransform())
                            .into(ivAvatar);
                }
                break;
            case UCrop.RESULT_ERROR:
                final Throwable cropError = UCrop.getError(data);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Obtenemos un random name para el caché de la imagen
     *
     * @return
     */
    private String getFileName() {
        return String.format("%s.jpg", System.currentTimeMillis());
    }

    /**
     * Método para enviar al backend el avatar
     */
    public void saveAvatar() {
        MultipartBody.Part fileObject = null;
        if (resultUri != null) {
            File file = new File(resultUri.getPath());
            RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            fileObject = MultipartBody.Part.createFormData("image", file.getName(), reqFile);
        }

        RestAdapter.getClient(activity).create(ProfileInterface.class).modifify_avatar(fileObject).enqueue(new RequestResponseDataJsonObject("profile") {
            @Override
            public void successful(JsonObject object) {

            }

            @Override
            public void successful(String msg) {
                UtilsImage.setCurrentDate(activity); // Actualizamos la key de la imagen del avatar
                new ManageGeneralRequest(activity).me();
            }

            @Override
            public void error(String msg) {
                AlertGlobal.showCustomError(activity, "Atención", msg);
            }
        });
    }

    /**
     * Método para enviar al backend los datos del profesional
     */
    public void saveProfile() {
        // Si los campos son válidos, generamos el Json para enviar al servidor
        JsonObject userObject = new JsonObject();

        userObject.addProperty("first_name", etFirstName.getText().toString());
        userObject.addProperty("last_name", etLastName.getText().toString());
        userObject.addProperty("birthdate", etBirthDate.getText().toString());
        userObject.addProperty("document", etDocument.getText().toString());


        RestAdapter.getClient(activity).create(ProfileInterface.class).modify(userObject).enqueue(new RequestResponseDataJsonObject("profile") {
            @Override
            public void successful(JsonObject object) {

            }

            @Override
            public void successful(String msg) {
                loading.dismissLoading();
                AlertGlobal.showCustomSuccess(activity, "Excelente", msg);
                new ManageGeneralRequest(activity).me();
            }

            @Override
            public void error(String msg) {
                loading.dismissLoading();
                AlertGlobal.showCustomError(activity, "Atención", msg);
            }
        });
    }
}
