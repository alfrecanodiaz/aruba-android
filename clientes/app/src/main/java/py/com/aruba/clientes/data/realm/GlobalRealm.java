package py.com.aruba.clientes.data.realm;

import io.realm.Realm;

public class GlobalRealm {
    private static Realm sRealm;

    /**
     * Método para obtener una instancia global de realm. Si la instancia ya está en memoria utilizamos esa, de lo contrario creamos una.
     * Si realm está en una transacción, cerramos la transacción antes de devolver la instancia. Porque nunca se le debe llamar a éste constructor desde una transacción
     * @return
     */
    public static Realm getDefault() {
        if (sRealm == null)
            sRealm = Realm.getDefaultInstance();
        else
            if (sRealm.isInTransaction())
                sRealm.commitTransaction();
        return sRealm;
    }
}
