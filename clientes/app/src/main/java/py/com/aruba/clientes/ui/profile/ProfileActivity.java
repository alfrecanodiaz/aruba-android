package py.com.aruba.clientes.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.eventbus.BusEvents;
import py.com.aruba.clientes.data.eventbus.GlobalBus;
import py.com.aruba.clientes.data.helpers.ResponseData;
import py.com.aruba.clientes.data.helpers.RestAdapter;
import py.com.aruba.clientes.data.helpers.request.ManageGeneralRequest;
import py.com.aruba.clientes.data.helpers.request.RequestResponseDataJsonObject;
import py.com.aruba.clientes.data.models.User;
import py.com.aruba.clientes.data.models.UserAddress;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.data.service.AddressInterface;
import py.com.aruba.clientes.data.service.ProfileInterface;
import py.com.aruba.clientes.ui.profile.mapsLocation.RegisterMapLocationActivity;
import py.com.aruba.clientes.utils.Constants;
import py.com.aruba.clientes.utils.Utils;
import py.com.aruba.clientes.utils.UtilsForm;
import py.com.aruba.clientes.utils.alerts.AlertGlobal;
import py.com.aruba.clientes.utils.dialogs.CustomDialog;
import py.com.aruba.clientes.utils.images.UtilsImage;
import py.com.aruba.clientes.utils.loading.UtilsLoading;
import retrofit2.Call;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.tvUsername)
    TextView tvUsername;
    @BindView(R.id.ivAvatar)
    ImageView ivAvatar;
    @BindView(R.id.etFirstName)
    EditText etFirstName;
    @BindView(R.id.etLastName)
    EditText etLastName;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etRUC)
    EditText etRUC;
    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.etRazonSocial)
    EditText etRazonSocial;
    @BindView(R.id.llAddressList)
    LinearLayout llAddressList;
    private HashMap<Integer, View> buttonsList;
    private Realm realm;
    private UtilsLoading utilsLoaing;
    private User user;
    private Uri resultUri = null;
    private AppCompatActivity activity;

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
        user = User.getUser(realm);
        setDataContext();
    }

    /**
     * Método para setear lo datos relacionados al usuario
     */
    private void setDataContext() {
        if (user == null) return;

        tvUsername.setText(String.format("¡Hola %s!", user.getFirst_name()));
        etFirstName.setText(user.getFirst_name());
        etLastName.setText(user.getLast_name());
        etEmail.setText(user.getEmail());
        if (!user.getRazon_social().isEmpty()) etRazonSocial.setText(user.getRazon_social());
        if (!user.getRuc().isEmpty()) etRUC.setText(user.getRuc());
        etPhone.setText(user.getPhone());
        UtilsImage.loadImageCircle(activity, ivAvatar, user.getAvatar(), Constants.PLACEHOLDERS.AVATAR);

        setButtonsList();
    }

    /**
     * Método para setear los botones de sub-categorias
     */
    private void setButtonsList() {
        llAddressList.removeAllViews();
        RealmResults<UserAddress> listAddress = UserAddress.getAll(realm);
        buttonsList = new HashMap<Integer, View>();

        // Asociamos el view al servicio
        for (UserAddress address : listAddress) {
            final View view = LayoutInflater.from(activity).inflate(R.layout.item_address_profile, null);
            Button btnAddress = view.findViewById(R.id.btnAddress);
            ImageView ivEdit = view.findViewById(R.id.ivEdit);
            ImageView ivDelete = view.findViewById(R.id.ivDelete);

            btnAddress.setText(String.format("%s: %s", address.getName(), address.getFullAddress()));

            // Seteamos el button por default y el clickeado
            btnAddress.setOnClickListener(v -> {
                Utils.preventTwoClick(v);
                UserAddress.resetAll(realm);
                UserAddress.setDefault(address.getId());
                updateAddressInBackend(address.getId());
                setDefaultAddressColor();
            });

            // Edit
            ivEdit.setOnClickListener(v -> {
                Utils.preventTwoClick(v);
                Intent intent = new Intent(activity, RegisterMapLocationActivity.class);
                intent.putExtra("go_to_main", false);
                intent.putExtra("address_id", address.getBackend_id());
                intent.putExtra("is_edit_mode", true);
                startActivity(intent);
            });

            // Delete
            ivDelete.setOnClickListener(v -> {
                Utils.preventTwoClick(v);
                AlertGlobal.showCustomError(activity, "Atención", "¿Está seguro que quiere borrar ésta dirección?", new CustomDialog.ButtonsListener() {
                    @Override
                    public void onDialogPositiveClick(DialogFragment dialog) {
                        deleteAddress(address.getBackend_id());
                    }

                    @Override
                    public void onDialogNegativeClick(DialogFragment dialog) {

                    }
                });
            });

            // Guardamos en el listado
            buttonsList.put(address.getId(), view);
            // Agregamos la vista al contenedor padre
            llAddressList.addView(view);
        }

        setDefaultAddressColor();
    }

    private void setDefaultAddressColor() {
        // Reiniciamos los colores de los demás botones y seteamos el actual
        for (int addressID : buttonsList.keySet()) {
            UserAddress address = realm.where(UserAddress.class).equalTo("id", addressID).findFirst();

            // Obtenemos los views
            Button btnAddress = buttonsList.get(addressID).findViewById(R.id.btnAddress);
            ImageView checkIcon = buttonsList.get(addressID).findViewById(R.id.ivSelected);


            if (address.isIs_default()) {
                btnAddress.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_aqua));
                checkIcon.setVisibility(View.VISIBLE);
            } else {
                btnAddress.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_silver));
                checkIcon.setVisibility(View.INVISIBLE);
            }
        }
    }

    @OnClick({R.id.rlBackButton, R.id.ivAddAddress, R.id.btnSave})
    public void onViewClicked(View view) {
        Utils.preventTwoClick(view);

        switch (view.getId()) {
            case R.id.rlBackButton:
                finish();
                break;
            case R.id.ivAddAddress:
                Intent intent = new Intent(activity, RegisterMapLocationActivity.class);
                intent.putExtra("go_to_main", false);
                startActivity(intent);
                break;
            case R.id.btnSave:
                if (isNotValidForm()) return;
                utilsLoaing = new UtilsLoading(ProfileActivity.this);
                utilsLoaing.showLoading();
                if (resultUri != null) saveAvatar();
                saveProfile();
                saveRuc();
                break;
        }
    }

    /**
     * Método para validar el formulario
     *
     * @return
     */
    private boolean isNotValidForm() {
        // Validamos nombre, apellido y telefono
        if (!UtilsForm.isTextValid(etFirstName, etFirstName.getText().toString())) return true;
        if (!UtilsForm.isTextValid(etLastName, etLastName.getText().toString())) return true;
        if (!UtilsForm.isTextValid(etPhone, etPhone.getText().toString())) return true;

        // Si se completa ruc, también se debe completar razon social
        if (TextUtils.isEmpty(etRUC.getText().toString()) && TextUtils.isEmpty(etRazonSocial.getText().toString())) {
            return false;
        } else {
            if (!TextUtils.isEmpty(etRUC.getText().toString()) && TextUtils.isEmpty(etRazonSocial.getText().toString())) {
                AlertGlobal.showCustomError(activity, "Atención", "Para guardar los datos de facturación, es necesario completar ambos campos");
                return true;
            } else {
                return false;
            }
        }
    }

    @OnClick(R.id.ivEditAvatar)
    public void onViewClicked() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onModelChange(BusEvents.ModelUpdated event) {
        if ("address".equals(event.model)) {
            setButtonsList();
        }
        if ("profile".equals(event.model)) {
            setDataContext();
        }
    }

    /**
     * Método para guardar un dispositivo
     */
    public void saveProfile() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("first_name", etFirstName.getText().toString());
        jsonObject.addProperty("last_name", etLastName.getText().toString());


        ProfileInterface restInterface = RestAdapter.getClient(activity).create(ProfileInterface.class);
        restInterface.update(jsonObject).enqueue(new RequestResponseDataJsonObject("profile") {
            @Override
            public void successful(JsonObject object) {
            }

            @Override
            public void successful(String msg) {
                realm.beginTransaction();
                user.setFirst_name(etFirstName.getText().toString());
                user.setLast_name(etLastName.getText().toString());
                user.setPhone(etPhone.getText().toString());
                realm.copyToRealmOrUpdate(user);
                realm.commitTransaction();

                // Enviamos el device, una vez que se actualiza el profile
                new ManageGeneralRequest(ProfileActivity.this).sendDevice();

                AlertGlobal.showCustomSuccess(ProfileActivity.this, "Excelente", msg);
                utilsLoaing.dismissLoading();
            }

            @Override
            public void error(String msg) {
                AlertGlobal.showCustomError(ProfileActivity.this, "Atención", msg);
                utilsLoaing.dismissLoading();
            }
        });
    }

    /**
     * Método para guardar un dispositivo
     */
    public void saveRuc() {
        if (etRazonSocial.getText().toString().isEmpty() || etRUC.getText().toString().isEmpty())
            return;
        boolean is_new = user.getRuc_id() == 0; // Si es igual a 0, entonces es nueva factura

        JsonObject data = new JsonObject();
        if (!is_new) data.addProperty("id", user.getRuc_id());
        data.addProperty("ruc_number", etRUC.getText().toString());
        data.addProperty("social_reason", etRazonSocial.getText().toString());


        Call<ResponseData<JsonElement>> call;

        if (is_new)
            call = RestAdapter.getClient(activity).create(ProfileInterface.class).ruc_create(data);
        else call = RestAdapter.getClient(activity).create(ProfileInterface.class).ruc_update(data);


        call.enqueue(new RequestResponseDataJsonObject("ruc") {
            @Override
            public void successful(JsonObject object) {

                realm.beginTransaction();
                if (!is_new) user.setRuc_id(object.get("id").getAsInt());
                user.setRazon_social(object.get("social_reason").getAsString());
                user.setRuc(object.get("ruc_number").getAsString());
                realm.copyToRealmOrUpdate(user);
                realm.commitTransaction();
            }

            @Override
            public void successful(String msg) {

            }

            @Override
            public void error(String msg) {
                AlertGlobal.showCustomError(ProfileActivity.this, "Atención", msg);
            }
        });
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
                new ManageGeneralRequest(activity).me();
            }

            @Override
            public void error(String msg) {
                AlertGlobal.showCustomError(activity, "Atención", msg);
            }
        });
    }

    /**
     * Método para actualizar la dirección default en el backend
     */
    private void updateAddressInBackend(int id){
        // Si los campos son válidos, generamos el Json para enviar al servidor
        JsonObject jsonData = new JsonObject();
        jsonData.addProperty("is_default", true);
        jsonData.addProperty("id", id);


        UtilsLoading loading = new UtilsLoading(this);
        loading.showLoading();

        AddressInterface restInterface = RestAdapter.getClient(activity).create(AddressInterface.class);
        restInterface.update(jsonData).enqueue(new RequestResponseDataJsonObject( "RegisterAddress") {
            @Override
            public void successful(JsonObject object) {
                loading.dismissLoading();

                JsonArray dataArray = new JsonArray();
                dataArray.add(object);
                UserAddress.createOrUpdateArrayBackground(dataArray);

                AlertGlobal.showCustomSuccess(ProfileActivity.this, "Excelente", "Se actualizó correctamente la dirección principal.");
            }

            @Override
            public void successful(String msg) {
                AlertGlobal.showCustomError(ProfileActivity.this, "Atención", msg);
                GlobalBus.getBus().post(new BusEvents.ModelUpdated("address"));
                loading.dismissLoading();
            }

            @Override
            public void error(String msg) {
                AlertGlobal.showCustomError(ProfileActivity.this, "Atención", msg);
                loading.dismissLoading();
            }
        });
    }

    /**
     * Método para actualizar la dirección default en el backend
     */
    private void deleteAddress(int id){
        // Si los campos son válidos, generamos el Json para enviar al servidor
        JsonObject jsonData = new JsonObject();
        jsonData.addProperty("id", id);


        UtilsLoading loading = new UtilsLoading(this);
        loading.showLoading();

        AddressInterface restInterface = RestAdapter.getClient(activity).create(AddressInterface.class);
        restInterface.delete(jsonData).enqueue(new RequestResponseDataJsonObject( "RegisterAddress") {
            @Override
            public void successful(JsonObject object) {
                loading.dismissLoading();
            }

            @Override
            public void successful(String msg) {
                AlertGlobal.showCustomError(ProfileActivity.this, "Atención", msg);
                realm.beginTransaction();
                realm.where(UserAddress.class).equalTo("backend_id", id).findFirst().deleteFromRealm();
                realm.commitTransaction();

                GlobalBus.getBus().post(new BusEvents.ModelUpdated("address"));

                loading.dismissLoading();
            }

            @Override
            public void error(String msg) {
                AlertGlobal.showCustomError(ProfileActivity.this, "Atención", msg);
                loading.dismissLoading();
            }
        });
    }

}
