package py.com.aruba.clientes.ui.cart;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.helpers.RestAdapter;
import py.com.aruba.clientes.data.helpers.request.RequestResponseDataJsonObject;
import py.com.aruba.clientes.data.helpers.request.RequestResponseDataPaginator;
import py.com.aruba.clientes.data.models.Appointment;
import py.com.aruba.clientes.data.models.AppointmentDetails;
import py.com.aruba.clientes.data.models.User;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.data.service.AppointmentInterface;
import py.com.aruba.clientes.ui.appointment.AppointmentReservationActivity;
import py.com.aruba.clientes.ui.cart.cartDialog.CartBancardDialog;
import py.com.aruba.clientes.ui.cart.cartDialog.CartDialog;
import py.com.aruba.clientes.ui.cart.recyclerSchedules.AppointmentViewAdapter;
import py.com.aruba.clientes.utils.Constants;
import py.com.aruba.clientes.utils.Print;
import py.com.aruba.clientes.utils.SharedPreferencesUtils;
import py.com.aruba.clientes.utils.TimeUtils;
import py.com.aruba.clientes.utils.Utils;
import py.com.aruba.clientes.utils.alerts.AlertGlobal;
import py.com.aruba.clientes.utils.dialogs.CustomDialog;
import py.com.aruba.clientes.utils.loading.UtilsLoading;

import static py.com.aruba.clientes.utils.UtilsParsing.getElement;


public class CartActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ivLogoToolbar)
    ImageView ivLogoToolbar;
    @BindView(R.id.webViewAndroid)
    WebView webView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rlBackButton)
    RelativeLayout rlBackButton;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.app_bar_content)
    RelativeLayout appBarContent;
    @BindView(R.id.etRUC)
    EditText etRUC;
    @BindView(R.id.etRazonSocial)
    EditText etRazonSocial;
    @BindView(R.id.viewSeparator1)
    View viewSeparator1;
    @BindView(R.id.etClientAmount)
    EditText etClientAmount;
    @BindView(R.id.llContentCash)
    LinearLayout llContentCash;
    @BindView(R.id.llContentTransfer)
    LinearLayout llContentTransfer;
    @BindView(R.id.llContentCard)
    LinearLayout llContentCard;
    @BindView(R.id.btnPaid)
    Button btnPaid;
    @BindView(R.id.tvTotalText)
    TextView tvTotalText;
    @BindView(R.id.tvTotalAmount)
    TextView tvTotalAmount;
    @BindView(R.id.rlContentTotal)
    RelativeLayout rlContentTotal;
    @BindView(R.id.tvButtonCash)
    TextView tvButtonCash;
    @BindView(R.id.rlButtonCash)
    RelativeLayout rlButtonCash;
    @BindView(R.id.tvButtonTransfer)
    TextView tvButtonTransfer;
    @BindView(R.id.rlButtonTransfer)
    RelativeLayout rlButtonTransfer;
    @BindView(R.id.tvButtonCard)
    TextView tvButtonCard;
    @BindView(R.id.tvCreditcardMessage)
    TextView tvCreditcardMessage;
    @BindView(R.id.rlButtonCard)
    RelativeLayout rlButtonCard;
    private Realm realm;
    private AppointmentViewAdapter scheduleViewAdapter;
    private AppCompatActivity activity;
    private UtilsLoading customLoading;
    private RealmResults<Appointment> listAppointmentsRealm;
    private int payment_type = 0;
    private double totalCartAmount;
    private User user;

    // Datos para mostrar el formulario de success en el caso de que sea con bancard
    String date;
    String shop_process_id;
    String amount;
    String response_description;
    private String BancardID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // PART1 - All activities must have this settings
        ButterKnife.bind(this);
        realm = GlobalRealm.getDefault();
        activity = this;
        customLoading = new UtilsLoading(CartActivity.this);

        // END PART1
        user = realm.where(User.class).findFirst();
        initRecycler();
        setDataContxt();
    }

    private void setDataContxt() {
        etRazonSocial.setText(user.getRazon_social());
        etRUC.setText(String.valueOf(user.getRuc()));
    }

    /**
     * Método para inicializar el recycler
     */
    private void initRecycler() {

        // Obtenemos todos los sub-servicios asociados a la categoría
        listAppointmentsRealm = realm.where(Appointment.class)
                .equalTo("status", Constants.APPOINTMENT_STATUS.IN_CART)
                .sort("id", Sort.DESCENDING)
                .findAll();
        List<Appointment> listAppointments = realm.copyFromRealm(listAppointmentsRealm);

        // Sumamos el amount del carrito
        for (Appointment ap : listAppointments) totalCartAmount += ap.getPrice();
        tvTotalAmount.setText(Utils.priceFormat(totalCartAmount));

        // Recycler View
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(mLayoutManager);
        scheduleViewAdapter = new AppointmentViewAdapter(activity, listAppointments);
        recyclerView.setAdapter(scheduleViewAdapter);
    }

    /**
     * Cargamos el webview cuando se clickea el boton de "bancard"
     *
     * @param data
     */
    private void loadWebView(JsonObject data) {
        String initial_url = "https://aruba.com.py/api/client/mobile/user/appointment/bancard" + getParams(data);

        // Headers de autenticación
        Map<String, String> extraHeaders = new HashMap<>();
        extraHeaders.put("Authorization", "Bearer " + SharedPreferencesUtils.getString(activity, Constants.TOKEN));
        Print.e("url", initial_url);

        webView.setVerticalScrollBarEnabled(true);
        webView.requestFocus();
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setPadding(0, 0, 0, 0);
        webView.setBackgroundColor(getResources().getColor(R.color.transparent));
        webView.setInitialScale(1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(initial_url, extraHeaders);
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");
        webView.setVisibility(View.VISIBLE);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Print.e("url change", url);

                // Si la url contiene el siguiente string, mostramos el mensaje de sucess y reenviamos
                if (url.toLowerCase().contains("payment_success")) {
                    customLoading.showLoading();
                    view.destroy();

                    // Descargamos el appointment y mostramos en el dialogo los datos del success
                    String url_response = url;
                    url_response = url_response.replace("https://aruba.com.py/api/bancard/", "");
                    url_response = url_response.replace("/success?status=payment_success", "");
                    int appointment_id = Integer.parseInt(url_response);

                    Appointment.deleteFinishedAppointments();
                    downloadAppointmentAndShowSuccess(appointment_id, 1);
                    return false;
                }

                if (url.toLowerCase().contains("payment_fail")) {
                    payment_type = 0;
                    rlButtonCard.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_empty));
                    tvButtonCard.setTextColor(ContextCompat.getColor(activity, R.color.lineBlack));
                    btnPaid.setVisibility(View.VISIBLE);
                    llContentCard.setVisibility(View.INVISIBLE);
                    webView.setVisibility(View.INVISIBLE);
                    tvCreditcardMessage.setVisibility(View.VISIBLE);
                    AlertGlobal.showCustomError(activity, "Error", "Hubo un error al momento de procesar el pago, por favor intente de nuevo");
                    return false;
                }

                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                customLoading.dismissLoading();
            }
        });

        webView.setWebChromeClient(new WebChromeClient());
    }

    // Convertimos el json en url params
    private String getParams(JsonObject data) {
        String url = "";
        url += "?professional_id=" + data.get("professional_id").getAsString();
        url += "&hour_start=" + data.get("hour_start").getAsString();
        url += "&address_id=" + data.get("address_id").getAsString();
        url += "&date=" + data.get("date").getAsString();
        url += "&client_name=" + data.get("client_name").getAsString();
        url += "&payment_type=2";
        // Iteramos por los servicios
        JsonArray services = data.get("services_id").getAsJsonArray();

        for (JsonElement service : services) {
            String id = service.getAsString();
            url += "&services_id[]=" + id;
        }

        return url;
    }

    /**
     * Método para enviar el appointment al backend
     *
     * @param data
     */
    private void sendToBackend(JsonObject data, boolean isLast) {
        RestAdapter.getClient(activity).create(AppointmentInterface.class).create(data).enqueue(new RequestResponseDataJsonObject("appointment") {
            @Override
            public void successful(JsonObject object) {
                JsonArray data = new JsonArray();
                data.add(object);
                Appointment.createOrUpdateArrayBackground(data);

                if (isLast) {
                    customLoading.dismissLoading();

                    // Mostramos el dialogo de que se agendó correctamente
                    CartDialog customDialog = new CartDialog();
                    customDialog.setData(activity);
                    customDialog.setCancelable(false);
                    customDialog.setListener(new CartDialog.ButtonsListener() {
                        @Override
                        public void onDialogPositiveClick(DialogFragment dialog) {
                            Appointment.deleteFinishedAppointments();
                            finish();
                        }

                        @Override
                        public void onDialogNegativeClick(DialogFragment dialog) {
                            finish();
                        }
                    });
                    customDialog.show(getSupportFragmentManager(), "dialog");
                }
            }

            @Override
            public void successful(String msg) {
                if (isLast) {
                    AlertGlobal.showCustomError(CartActivity.this, "Atención", msg);
                    customLoading.dismissLoading();
                }
            }

            @Override
            public void error(String msg) {
                AlertGlobal.showCustomError(CartActivity.this, "Atención", msg);
                customLoading.dismissLoading();
            }
        });
    }

    /**
     * Método para cancelar la reserva de bancard
     */
    private void cancelBancard() {
        customLoading.showLoading();
        JsonObject data = new JsonObject();
        data.addProperty("shop_process_id", BancardID);

        RestAdapter.getClient(activity).create(AppointmentInterface.class).cancel_bancard(data).enqueue(new RequestResponseDataJsonObject("bancard") {
            @Override
            public void successful(JsonObject object) {
            }

            @Override
            public void successful(String msg) {
                rlButtonCard.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_empty));
                tvButtonCard.setTextColor(ContextCompat.getColor(activity, R.color.lineBlack));
                btnPaid.setVisibility(View.VISIBLE);
                llContentCard.setVisibility(View.INVISIBLE);
                tvCreditcardMessage.setVisibility(View.VISIBLE);
                webView.setVisibility(View.INVISIBLE);
                BancardID = "";
                customLoading.dismissLoading();
            }

            @Override
            public void error(String msg) {
                AlertGlobal.showCustomError(CartActivity.this, "Atención", msg);
            }
        });
    }

    /**
     * Método para armar el json que se enviará al backend para guardar el appointment
     *
     * @return
     */
    private JsonObject getAppointmentJsonObject(Appointment appointment) {

        JsonObject data = new JsonObject();
        data.addProperty("professional_id", appointment.getProfessional_id());
        data.addProperty("hour_start", appointment.getFrom_hour());
        data.addProperty("address_id", appointment.getAddress_id());
        data.addProperty("date", TimeUtils.getDateString(appointment.getDate()));
        data.addProperty("client_name", appointment.getClient());
        data.addProperty("payment_type", payment_type);
        if (payment_type == Constants.PAYMENT_TYPE.CASH) {
            data.addProperty("client_amount", Integer.parseInt(etClientAmount.getText().toString()));
        }


        // Obtenemos los detalles del appointment (id del servicio)
        RealmResults<AppointmentDetails> appointmentDetails = AppointmentDetails.getAllDetails(realm, appointment);
        JsonArray details = new JsonArray();
        for (AppointmentDetails sd : appointmentDetails) details.add(sd.getSub_service_id());

        // Agregamos los ID de los subservicios al objeto
        data.add("services_id", details);

        return data;
    }

    @OnClick({R.id.rlBackButton, R.id.btnPaid})
    public void onViewClicked(View view) {
        Utils.preventTwoClick(view);
        switch (view.getId()) {
            case R.id.rlBackButton:
                AlertGlobal.showCustomError(CartActivity.this, "Atención", "¿Está seguro que quiere salir del proceso de agendar un servicio?", new CustomDialog.ButtonsListener() {
                    @Override
                    public void onDialogPositiveClick(DialogFragment dialog) {
                        CartActivity.this.finish();
                    }

                    @Override
                    public void onDialogNegativeClick(DialogFragment dialog) {

                    }
                });
                break;
            case R.id.btnPaid:
                if (payment_type == Constants.PAYMENT_TYPE.CASH) {
                    AlertGlobal.showMessage(activity, "Atención", "Acá falta definir la cantidad de puntos que se usa para cada servicio solicitado");
                    return;
                }
                if (payment_type == Constants.PAYMENT_TYPE.CARD) {
                    customLoading.showLoading();

                    btnPaid.setVisibility(View.INVISIBLE);
                    webView.setVisibility(View.VISIBLE);
                    tvCreditcardMessage.setVisibility(View.GONE);

                    // Cargamos el webview con los datos del pago con tarjeta
                    loadWebView(getAppointmentJsonObject(listAppointmentsRealm.get(0)));
                    return;
                }
                if (validateForm()) return;
                customLoading.showLoading();

                // Iteramos por todos los schedules

                for (int i = 0; i < listAppointmentsRealm.size(); i++) {
                    Appointment sc = listAppointmentsRealm.get(i);
                    sendToBackend(getAppointmentJsonObject(sc), (i + 1 == listAppointmentsRealm.size()));
                }
                break;
        }
    }

    @OnClick({R.id.rlButtonCash, R.id.rlButtonTransfer, R.id.rlButtonCard})
    public void onMethodPaidClicked(View view) {

        // Ocultamos los detalles
        llContentCash.setVisibility(View.GONE);
        llContentTransfer.setVisibility(View.GONE);
        llContentCard.setVisibility(View.GONE);

        // Cambiamos el color de los botones
        rlButtonCash.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_empty));
        rlButtonTransfer.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_empty));
        rlButtonCard.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_empty));

        // Cambiamos el color del texto
        tvButtonCash.setTextColor(ContextCompat.getColor(activity, R.color.lineBlack));
        tvButtonTransfer.setTextColor(ContextCompat.getColor(activity, R.color.lineBlack));
        tvButtonCard.setTextColor(ContextCompat.getColor(activity, R.color.lineBlack));

        switch (view.getId()) {
            // Pntos
            case R.id.rlButtonCash:
                // Si el tipo de pago actual es bancard, cancelamos
                if (payment_type == Constants.PAYMENT_TYPE.CARD && !BancardID.equals("")) {
                    cancelBancard();
                }

                payment_type = Constants.PAYMENT_TYPE.CASH;
                llContentCash.setVisibility(View.VISIBLE);
                rlButtonCash.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_aqua));
                tvButtonCash.setTextColor(ContextCompat.getColor(activity, R.color.white));
                btnPaid.setVisibility(View.VISIBLE);
                break;
            case R.id.rlButtonTransfer:
                // Si el tipo de pago actual es bancard, cancelamos
                if (payment_type == Constants.PAYMENT_TYPE.CARD && !BancardID.equals("")) {
                    cancelBancard();
                }

                payment_type = Constants.PAYMENT_TYPE.TRANSFER;
                llContentTransfer.setVisibility(View.VISIBLE);
                rlButtonTransfer.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_aqua));
                tvButtonTransfer.setTextColor(ContextCompat.getColor(activity, R.color.white));

                btnPaid.setVisibility(View.VISIBLE);
                break;
            case R.id.rlButtonCard:
                payment_type = Constants.PAYMENT_TYPE.CARD;
                llContentCard.setVisibility(View.VISIBLE);
                rlButtonCard.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_aqua));
                tvButtonCard.setTextColor(ContextCompat.getColor(activity, R.color.white));

                btnPaid.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        AlertGlobal.showCustomError(CartActivity.this, "Atención", "¿Está seguro que quiere salir del proceso de agendar un servicio?", new CustomDialog.ButtonsListener() {
            @Override
            public void onDialogPositiveClick(DialogFragment dialog) {
                CartActivity.this.finish();
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {

            }
        });
    }

    /**
     * Método para descargar todos los appointments y motrar el mensaje de success
     *
     * @param appointment_id
     * @param page
     */
    private void downloadAppointmentAndShowSuccess(int appointment_id, int page) {

        AppointmentInterface restInterface = RestAdapter.getClient(activity).create(AppointmentInterface.class);
        restInterface.list(page).enqueue(new RequestResponseDataPaginator(page, "appointment") {
            @Override
            public void successful(JsonArray dataArray, int page, boolean hasNextPage) {

                // Verificamos si el ID del appointment coincide con lo que recibimos
                for (JsonElement e : dataArray) {
                    JsonObject data = e.getAsJsonObject();
                    if (appointment_id == getElement(data, "id", 0)) {
                        JsonObject transaction = data.get("transaction").getAsJsonObject();
                        JsonObject transactionable = transaction.get("transactionable").getAsJsonObject();
                        date = getElement(transactionable, "created_at", "");
                        shop_process_id = getElement(transactionable, "shop_process_id", "");
                        amount = getElement(transactionable, "amount", "");
                        response_description = getElement(transactionable, "response_description", "");
                    }
                }

                // Descargamos y guardamos todos
                Appointment.createOrUpdateArrayBackground(dataArray);
                if (hasNextPage) {
                    downloadAppointmentAndShowSuccess(appointment_id, page);
                } else {


                    // Mostramos el dialogo de que se agendó correctamente
                    CartBancardDialog customDialog = new CartBancardDialog();
                    customDialog.setCancelable(false);
                    customDialog.setData(activity, date, shop_process_id, amount, response_description);
                    customDialog.setListener(new CartDialog.ButtonsListener() {
                        @Override
                        public void onDialogPositiveClick(DialogFragment dialog) {
                            Appointment.deleteFinishedAppointments();
                            finish();
                        }

                        @Override
                        public void onDialogNegativeClick(DialogFragment dialog) {
                            finish();
                        }
                    });
                    customDialog.show(getSupportFragmentManager(), "dialog");
                }
            }

            @Override
            public void error(String msg) {

            }
        });


    }

    /**
     * Método para validar los campos del carrito. Si retorna true, paramos, si retorna false continuamos
     *
     * @return
     */
    private boolean validateForm() {
        // Validamos el monto mínimo
        if (totalCartAmount < Constants.MINIMAL_AMOUNT) {
            AlertGlobal.showCustomError(CartActivity.this, "Atención", "Lo siento, el monto mínimo para solicitar el servicio es de 70.000 Gs.");
            return true;
        }

        // Validamos tipo de pago
        if (payment_type == 0) {
            AlertGlobal.showCustomError(CartActivity.this, "Atención", "Por favor seleccione el tipo de pago");
            return true;
        }

        // Validamos cash
        if (payment_type == Constants.PAYMENT_TYPE.CASH) {
            if (etClientAmount.getText().toString().isEmpty()) {
                AlertGlobal.showCustomError(CartActivity.this, "Atención", "Por favor complete el campo 'cambio de'");
                return true;
            }

            if (Integer.parseInt(etClientAmount.getText().toString()) < totalCartAmount) {
                AlertGlobal.showCustomError(CartActivity.this, "Atención", "El monto ingresado debe ser mayor al total");
                return true;
            }
        }
        return false;
    }

    /**
     * Interface para obtener el ID de bancard
     */
    public class WebAppInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /**
         * Show a toast from the web page
         */
        @JavascriptInterface
        public void sendID(String txt) {
            BancardID = txt;
        }
    }
}
