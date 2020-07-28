package py.com.aruba.clientes.data.realm;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class MyMigration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        if (oldVersion == 0) {
            // Agregamos la clase de horarios de profesionales
            schema.create("ProfessionalAvailableHours")
                    .addField("id", Integer.class, FieldAttribute.PRIMARY_KEY)
                    .addField("professional_id", Integer.class)
                    .addField("hour_start", Integer.class)
                    .addField("hour_end", Integer.class);

            oldVersion++;
        }
    }
}