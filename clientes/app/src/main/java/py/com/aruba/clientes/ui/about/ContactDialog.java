package py.com.aruba.clientes.ui.about;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import py.com.aruba.clientes.R;
import py.com.aruba.clientes.utils.dialogs.CustomDialog;

public class ContactDialog extends CustomDialog {

    private AppCompatActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_contact, container, false);
        ButterKnife.bind(this, view);
        return view;
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
    }

    @OnClick({R.id.ivClose, R.id.btnCall, R.id.btnMail, R.id.btnWeb })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivClose:
                ContactDialog.this.dismiss();
                break;
            case R.id.btnCall:
                call();
                break;
            case R.id.btnMail:
                mail();
                break;
            case R.id.btnWeb:
                web();
                break;
        }
    }

    private void web() {
        String url = "https://aruba.com.py";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }


    private void call() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + "595985363437"));
        getActivity().startActivity(intent);
    }

    private void mail() {
        // Preparamos el intent
        final Intent intentGmailLocal = new Intent(Intent.ACTION_SEND);
        intentGmailLocal.setType("text/plain");
        intentGmailLocal.putExtra(Intent.EXTRA_EMAIL, new String[]{"hola@aruba.com.py"});
        intentGmailLocal.putExtra(Intent.EXTRA_SUBJECT, "Consultas - APP");

        // Buscamos el paquete de gmail
        final PackageManager pmLocal = activity.getPackageManager();
        final List<ResolveInfo> matchesLocal = pmLocal.queryIntentActivities(intentGmailLocal, 0);
        ResolveInfo resolveInfoGmailLocal = null;
        for (final ResolveInfo info : matchesLocal) {
            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                resolveInfoGmailLocal = info;
        }

        // Si existe el paquete, abrimos la app, de lo contrario, abrimos un chooser
        if (resolveInfoGmailLocal != null) {
            intentGmailLocal.setClassName(resolveInfoGmailLocal.activityInfo.packageName, resolveInfoGmailLocal.activityInfo.name);
            startActivity(intentGmailLocal);
        } else {
            startActivity(Intent.createChooser(intentGmailLocal, "Por favor seleccione la aplicación"));
        }
    }

    public void setData(AppCompatActivity mainActivity) {
        activity = mainActivity;
    }
}
