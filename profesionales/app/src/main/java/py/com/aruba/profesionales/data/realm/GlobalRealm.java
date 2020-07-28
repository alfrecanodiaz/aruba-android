package py.com.aruba.profesionales.data.realm;

import io.realm.Realm;

public class GlobalRealm {
    private static Realm sRealm;

    public static Realm getDefault() {
        if (sRealm == null)
            sRealm = Realm.getDefaultInstance();
        if (sRealm.isInTransaction())
            sRealm.commitTransaction();
        return sRealm;
    }
}
