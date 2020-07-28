package py.com.aruba.clientes.utils.loading;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Utilizamos un activity como dialogo/loading para poder llamar desde cualquier lugar sin necesidad de que dependa de un context
 */
public class UtilsLoading {

    private AppCompatActivity activity;
    private CustomLoadingDialog df;

    public UtilsLoading(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void showLoading() {
//        activity.startActivity(new Intent(activity, CustomLoadingActivity.class));
        df = new CustomLoadingDialog();
        df.show(activity.getSupportFragmentManager(), "dialog");
    }

    public void dismissLoading() {
        if (df != null) {
            df.dismiss();
        }
    }
}
