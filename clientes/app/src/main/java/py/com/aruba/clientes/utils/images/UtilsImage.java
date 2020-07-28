package py.com.aruba.clientes.utils.images;


import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import py.com.aruba.clientes.R;
import py.com.aruba.clientes.utils.Constants;
import py.com.aruba.clientes.utils.SharedPreferencesUtils;

public class UtilsImage {


    /**
     * Método para cargar una imagen en imageview con Picasso
     *
     * @param context
     * @param imgView
     * @param path
     */
    public static void loadImage(Context context, ImageView imgView, String path) {
        if (path.isEmpty()) return;
        Glide.with(context)
                .load(path)
                .centerCrop()
//                .placeholder(drawable)
//                .error(drawable)
//                .fallback(drawable)
                .into(imgView);
    }


    /**
     * Método para cargar una imagen en imageview con Picasso
     *
     * @param context
     * @param imgView
     */
    public static void loadImageCircle(Context context, ImageView imgView, String path, int type) {
        // TODO: Esta logica se puede mejorar
        int drawable = (type == Constants.PLACEHOLDERS.AVATAR) ? R.drawable.avatar : R.drawable.icon_services;

        if (path.isEmpty()) return;
        Glide.with(context)
                .load(path)
                .centerCrop()
                .placeholder(drawable)
                .error(drawable)
                .fallback(drawable)
                .apply(RequestOptions.circleCropTransform())
                .into(imgView);
    }

    public static void loadImageCircle(Context context, ImageView imgView, String path) {
        if (path.isEmpty()) return;
        Glide.with(context)
                .load(path)
                .centerCrop()
//                .placeholder(drawable)
//                .error(drawable)
//                .fallback(drawable)
                .apply(RequestOptions.circleCropTransform())
                .into(imgView);
    }

    public static void loadImageCircleNoCache(Context context, ImageView imgView, String path) {
        if (path.isEmpty()) return;
        Glide.with(context)
                .load(path)
                .centerCrop()
                .thumbnail(Glide.with(context).load(path))
                .signature(new ObjectKey(getDayDate(context)))
                .apply(RequestOptions.circleCropTransform())
                .into(imgView);
    }

    // metodo para devolver la fecha actual para actualizar el cache de las imagenes una vez al dia
    private static String getDayDate(Context context) {
        return SharedPreferencesUtils.getString(context, "cache_date");
    }

    // Metodo para setear el current day cuando se inicializa la app (a este metodo se le llama desde el mainactivity)
    public static void setCurrentDate(Context context) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");
        String date = mdformat.format(calendar.getTime());
        SharedPreferencesUtils.setValue(context, "cache_date", date);
    }
}
