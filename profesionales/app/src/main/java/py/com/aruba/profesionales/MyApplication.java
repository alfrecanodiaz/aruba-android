package py.com.aruba.profesionales;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.facebook.stetho.Stetho;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;

public class MyApplication extends MultiDexApplication {
    public static boolean activityVisible;
    public static long session = System.currentTimeMillis();
    public static boolean MigrationNeeded = true;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // BD Local
        Realm.init(getApplicationContext());
        RealmConfiguration realmConfiguration = null;
        realmConfiguration = new RealmConfiguration.Builder()
                .name("aruba.realm")
                .build();

        // Verificamos si realm puede obtener la instancia o necesita realizar una migración.
        // De ser así, activamos la bandera de que se deben descargar todos los datos de nuevo, elminamos la BD y volvemos a crear.
        // En este caso no es necesario realizar migraciones de manera local, porque los datos guardados localmente, se replican en el backend
        try {
            MigrationNeeded = false;
            Realm.getInstance(realmConfiguration);
        } catch (RealmMigrationNeededException e) {
            MigrationNeeded = true;
            Realm.deleteRealm(realmConfiguration);
            Realm.getInstance(realmConfiguration);
        }

        Realm.setDefaultConfiguration(realmConfiguration);
        Realm.compactRealm(realmConfiguration);


        // Debug de la DB
        if (BuildConfig.DEBUG) {
            Stetho.initialize(Stetho.newInitializerBuilder(this)
                    .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                    .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                    .build());
        }

        // Librería para manejo de datetimes
        AndroidThreeTen.init(this);
        XLog.init(BuildConfig.DEBUG ? LogLevel.ALL : LogLevel.NONE);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static boolean isActivityVisible() {
        return activityVisible; // return true or false
    }

    public static void activityResumed() {
        activityVisible = true;// this will set true when activity resumed

    }

    public static void activityPaused() {
        activityVisible = false;// this will set false when activity paused

    }
}
