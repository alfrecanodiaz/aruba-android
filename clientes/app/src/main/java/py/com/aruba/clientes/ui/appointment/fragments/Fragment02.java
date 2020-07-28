package py.com.aruba.clientes.ui.appointment.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import jp.wasabeef.recyclerview.adapters.SlideInRightAnimationAdapter;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.eventbus.BusEvents;
import py.com.aruba.clientes.data.eventbus.GlobalBus;
import py.com.aruba.clientes.data.helpers.RestAdapter;
import py.com.aruba.clientes.data.helpers.request.RequestResponseDataJsonArray;
import py.com.aruba.clientes.data.models.Appointment;
import py.com.aruba.clientes.data.models.AppointmentDetails;
import py.com.aruba.clientes.data.models.Professional;
import py.com.aruba.clientes.data.models.ProfessionalAvailableHours;
import py.com.aruba.clientes.data.models.UserAddress;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.data.service.ProfessionalInterface;
import py.com.aruba.clientes.ui.appointment.AppointmentReservationActivity;
import py.com.aruba.clientes.ui.appointment.fragments.recyclerProfessionals.ProfessionalViewAdapter;
import py.com.aruba.clientes.utils.CustomCalendar.recycler.CalendarViewAdapter;
import py.com.aruba.clientes.utils.Print;
import py.com.aruba.clientes.utils.TimeUtils;
import py.com.aruba.clientes.utils.alerts.AlertGlobal;
import py.com.aruba.clientes.utils.loading.UtilsLoading;

public class Fragment02 extends Fragment {


    @BindView(R.id.tvMessage)
    TextView tvMessage;
    @BindView(R.id.tvMonth)
    TextView tvMonth;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.recyclerCalendarView)
    RecyclerView recyclerCalendarView;
    private Appointment appointment;

    private AppointmentReservationActivity activity;
    private ProfessionalViewAdapter professionalViewAdapter;
    private Realm realm;
    public boolean canSwipe;
    public String message = "Por favor selecciona la fecha y la hora para";
    private String query = "";
    private List<Professional> listProfessional;
    private JsonArray filteredProfessionalData;
    private UtilsLoading loading;
    private Date selectedDate;
    private int service_duration;

    public static Fragment02 newInstance() {
        return new Fragment02();
    }

    public interface OnCalendarItemClicked {
        void onCalendarItemClicked(JsonObject item);
    }

    public interface OnHourItemClicked {
        void onHourItemClicked(JsonObject item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_schedule_02_professional, container, false);
        ButterKnife.bind(this, view);

        activity = (AppointmentReservationActivity) getActivity();
        realm = GlobalRealm.getDefault();

        appointment = realm.where(Appointment.class).equalTo("id", activity.APPOINTMENT_ID).findFirst();

        setServiceDuration();
        initCalendarRecycler();

        return view;
    }

    /**
     * Seteamos la duración del servicio
     */
    private void setServiceDuration() {
        service_duration = 0;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        GlobalBus.getBus().register(this);
        super.onAttach(context);
    }

    /**
     * Método para inicializar el recycler del calendario
     */
    private void initCalendarRecycler() {
        List<JsonObject> list = new ArrayList<>();

        // Inicializamos el calendario. E Iteramos 90 veces para generar el calendario
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        for (int i = 0; i < 90; i++) {
            JsonObject o = new JsonObject();
            o.addProperty("dayMonth", calendar.get(Calendar.DAY_OF_MONTH));
            o.addProperty("dayWeek", calendar.get(Calendar.DAY_OF_WEEK));
            o.addProperty("month", calendar.get(Calendar.MONTH));
            o.addProperty("monthStr", TimeUtils.getMonth(calendar.get(Calendar.MONTH) + 1) + " " + calendar.get(Calendar.YEAR));
            o.addProperty("year", calendar.get(Calendar.YEAR));
            list.add(o);

            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        // Obtenemos el primer elemento y seteamos como el mes actual
        tvMonth.setText(list.get(0).get("monthStr").getAsString());

        // Acá inicializamos el recycler del calendario
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        recyclerCalendarView.setVisibility(View.VISIBLE);
        recyclerCalendarView.setHasFixedSize(true);
        recyclerCalendarView.setLayoutManager(mLayoutManager);
        CalendarViewAdapter calendarViewAdater = new CalendarViewAdapter(activity, list, item -> {
            onDateSet(item.get("year").getAsInt(), item.get("month").getAsInt(), item.get("dayMonth").getAsInt());
        });
        recyclerCalendarView.setAdapter(calendarViewAdater);

        // Custom animation
        recyclerCalendarView.setAdapter(new SlideInRightAnimationAdapter(calendarViewAdater));
        recyclerCalendarView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int itemVisible = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    tvMonth.setText(list.get(itemVisible).get("monthStr").getAsString());
                }
            }
        });
        LinearSnapHelper linearSnapHelper = new LinearSnapHelper();
        linearSnapHelper.attachToRecyclerView(recyclerCalendarView);
    }

    /**
     * Método para inicializar los datos
     */
    public void initData() {
        tvMessage.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        canSwipe = false;
    }

    /**
     * Cargamos en memoria los datos desde la BD
     */
    private void filterDataBase() {
        Integer[] listIDs = new Integer[filteredProfessionalData.size()];

        // Obtenemos los profesionales que coinciden con los criterios de los filtros
        for (int i = 0; i < filteredProfessionalData.size(); i++) {
            JsonObject object = filteredProfessionalData.get(i).getAsJsonObject();
            listIDs[i] = object.get("id").getAsInt();
        }

        RealmResults<Professional> listProfessionalRealm = realm.where(Professional.class).in("id", listIDs).findAll();

        // Filtramos si es que el query no está vacio
        if (!query.isEmpty()) {
            listProfessionalRealm = listProfessionalRealm.where()
                    .contains("search_data", query, Case.INSENSITIVE)
                    .findAll();
        }
        listProfessional = realm.copyFromRealm(listProfessionalRealm);
    }

    /**
     * Método para inicializar el recycler
     */
    private void initProfessionalRecycler() {
        recyclerView.setVisibility(View.VISIBLE);

        // Recycler View
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(mLayoutManager);
        professionalViewAdapter = new ProfessionalViewAdapter(activity, listProfessional, activity.APPOINTMENT_ID, service_duration, item -> {
            canSwipe = true;
            realm.beginTransaction();
            appointment.setProfessional_id(item.get("professional_id").getAsInt());
            appointment.setProfessional(item.get("professional").getAsString());
            appointment.setDate(selectedDate);
            appointment.setFrom_hour(item.get("hour_start").getAsInt());
            appointment.setHour_start_pretty(item.get("readable_hour").getAsString());
            realm.commitTransaction();
        });
        recyclerView.setAdapter(professionalViewAdapter);
        // Deshabilitamos la animación de fade al momento de notificar un cambio en el recycler
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        // Custom animation
        recyclerView.setAdapter(new SlideInRightAnimationAdapter(professionalViewAdapter));
    }

    /**
     * Método para actualizar el recyclerview, luego de filtrarse
     */
    private void updateRecycler() {
        professionalViewAdapter.update(listProfessional);
    }

    /**
     * Método para descargar los profesionales disponibles
     *
     * @param page
     * @param data
     */
    private void downloadProfessional(int page, JsonObject data) {
        ProfessionalInterface restInterface = RestAdapter.getClient(activity).create(ProfessionalInterface.class);
        restInterface.get_filtered_professionals(page, data).enqueue(new RequestResponseDataJsonArray("professional") {
            @Override
            public void successful(JsonArray dataArray) {
                if (dataArray.size() == 0) {
                    AlertGlobal.showCustomError(activity, "Atención", "No hay profesionales disponibles");
                } else {
                    filteredProfessionalData = dataArray;

                    // Guardamos los horarios de los profesionales
                    ProfessionalAvailableHours.delete(realm);
                    for (JsonElement d : filteredProfessionalData) {
                        JsonObject professional = d.getAsJsonObject();
                        ProfessionalAvailableHours.create(realm, professional.get("availableSchedules").getAsJsonArray(), professional.get("id").getAsInt());
                    }

                    filterDataBase();
                    initProfessionalRecycler();
                }
                loading.dismissLoading();
            }

            @Override
            public void successful(String msg) {
                AlertGlobal.showCustomError(activity, "Atención", msg);
            }

            @Override
            public void error(String msg) {
                AlertGlobal.showCustomError(activity, "Atención", msg);
                loading.dismissLoading();
            }
        });
    }

    /**
     * Método para armar el json que se enviará al backend para filtrar los profesionales
     *
     * @return
     */
    private JsonObject getProfessionalFilterData() {
        // Obtenemos la dirección que se guardó en el appointment
        UserAddress userAddress = realm.where(UserAddress.class).equalTo("backend_id", appointment.getAddress_id()).findFirst();
        service_duration = 0;

        JsonObject data = new JsonObject();
        data.addProperty("address_id", userAddress.getBackend_id());
        data.addProperty("lat", userAddress.getLat());
        data.addProperty("lng", userAddress.getLng());
        data.addProperty("date", TimeUtils.getDateString(selectedDate)); // "d-m-Y"

        // Obtenemos los detalles del appointment (id del servicio)
        RealmResults<AppointmentDetails> appointmentDetails = realm.where(AppointmentDetails.class).equalTo("appointment.id", appointment.getId()).findAll();
        JsonArray details = new JsonArray();
        for (AppointmentDetails sd : appointmentDetails) {
            details.add(sd.getSub_service_id());
            service_duration += sd.getSubService().getTime();
        }

        data.add("services", details);
        return data;
    }

    /**
     * Método para setear la fecha seleccionada y descargar los profesionales
     *
     * @param year
     * @param monthOfYear
     * @param dayOfMonth
     */
    public void onDateSet(int year, int monthOfYear, int dayOfMonth) {
        Print.e("dias", dayOfMonth);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        selectedDate = cal.getTime();
        tvMessage.setVisibility(View.INVISIBLE);
        message = "Por favor selecciona el horario de un profesional";
        canSwipe = false;

        // Inicializamos
        filteredProfessionalData = new JsonArray();

        // Search Dialog
        loading = new UtilsLoading(activity);
        loading.showLoading();

        // Filtro
        downloadProfessional(1, getProfessionalFilterData());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onQueryChange(BusEvents.ScheduleSearchQuery event) {
        this.query = event.string;
        filterDataBase();
        updateRecycler();
    }
}