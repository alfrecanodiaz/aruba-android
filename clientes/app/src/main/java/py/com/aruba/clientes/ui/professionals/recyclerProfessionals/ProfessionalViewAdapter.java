package py.com.aruba.clientes.ui.professionals.recyclerProfessionals;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import java.util.List;

import io.realm.Realm;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.data.eventbus.BusEvents;
import py.com.aruba.clientes.data.eventbus.GlobalBus;
import py.com.aruba.clientes.data.helpers.RestAdapter;
import py.com.aruba.clientes.data.helpers.request.RequestResponseDataJsonObject;
import py.com.aruba.clientes.data.models.Appointment;
import py.com.aruba.clientes.data.models.Professional;
import py.com.aruba.clientes.data.realm.GlobalRealm;
import py.com.aruba.clientes.data.service.ProfessionalInterface;
import py.com.aruba.clientes.ui.professionals.ProfessionalDetailActivity;
import py.com.aruba.clientes.utils.Constants;
import py.com.aruba.clientes.utils.Utils;
import py.com.aruba.clientes.utils.alerts.AlertGlobal;
import py.com.aruba.clientes.utils.images.UtilsImage;
import py.com.aruba.clientes.utils.loading.UtilsLoading;

public class ProfessionalViewAdapter extends RecyclerView.Adapter<ProfessionalViewHolder> {
    private static final String TAG = "ReviewsViewAdapter";

    // Variables
    private final Context context;
    private final AppCompatActivity activity;
    private List<Professional> lista;
    private Realm realm;
    private Appointment appointment;
    private RelativeLayout selectedItem;

    public ProfessionalViewAdapter(Context context, List<Professional> lista, AppCompatActivity activity) {
        this.context = context;
        this.activity = activity;
        this.lista = lista;
        this.realm = GlobalRealm.getDefault();
        appointment = realm.where(Appointment.class).findFirst();
    }

    @Override
    public ProfessionalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(context).inflate(R.layout.item_list_professional, null);
        return new ProfessionalViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(final ProfessionalViewHolder holder, int position) {
        final Professional object = lista.get(holder.getAdapterPosition());

        holder.tvName.setText(object.getFullName());
        holder.tvLikes.setText(String.valueOf(object.getLikes_count()));
        holder.tvServices.setText(String.valueOf(object.getServices_count()));
        holder.ratingBar.setNumStars(Integer.parseInt(object.getAverage_reviews()));

        UtilsImage.loadImageCircle(context, holder.ivAvatar, object.getAvatar(), Constants.PLACEHOLDERS.AVATAR);

        int heart = (object.isLiked()) ? R.drawable.like : R.drawable.unlike;
        holder.ivLike.setImageResource(heart);

        // Método onclick del like
        holder.ivLike.setOnClickListener(v -> {
            Utils.preventTwoClick(v);

            UtilsLoading ul = new UtilsLoading(activity);
            ul.showLoading();

            JsonObject data = new JsonObject();
            data.addProperty("professional_id",  object.getId());

            ProfessionalInterface restInterface = RestAdapter.getClient(activity).create(ProfessionalInterface.class);
            restInterface.set_like(data).enqueue(new RequestResponseDataJsonObject("like") {
                @Override
                public void successful(JsonObject object) {

                }

                @Override
                public void successful(String msg) {
                    ul.dismissLoading();
                    AlertGlobal.showCustomSuccess(activity, "Excelente", msg);

                    boolean liked = object.isLiked();
                    int like_count = object.getLikes_count();

                    // Si esta likeado, restamos, de lo contrario sumamos
                    if (liked && like_count > 0) {
                        holder.tvLikes.setText(String.valueOf(--like_count));
                        holder.ivLike.setImageResource(R.drawable.unlike);
                    } else {
                        holder.tvLikes.setText(String.valueOf(++like_count));
                        holder.ivLike.setImageResource(R.drawable.like);
                    }

                    // Actualizamos el objeto de la BD
                    realm.beginTransaction();
                    object.setLiked(!liked);
                    object.setLikes_count(like_count);
                    realm.copyToRealmOrUpdate(object);
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

        // Método onclick del item del recycler
        holder.llItem.setOnClickListener(v -> {
            Utils.preventTwoClick(v);

            Intent intent = new Intent(context, ProfessionalDetailActivity.class);
            intent.putExtra("ID", object.getId());

            // Shared transition
            Pair<View, String> p1 = Pair.create(holder.tvName, "tvProfessionalName");
            Pair<View, String> p2 = Pair.create(holder.ivAvatar, "ivAvatar");
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, p1, p2);

            context.startActivity(intent, options.toBundle());

        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void update(List<Professional> listReviews) {
        this.lista = listReviews;
        this.notifyDataSetChanged();
    }

    public void removeAll() {
        notifyItemRangeRemoved(0, lista.size() - 1);
    }
}
