package py.com.aruba.profesionales.data.models;

import io.realm.Realm;
import io.realm.RealmObject;


public class BaseModel<T> {


    Class model;
    Realm realm;

    public BaseModel(Class<T> type, Realm realm) {
        this.model = type;
        this.realm = realm;
    }

    public Class getModel(){
        return model;
    }

    /**
     * Retornamos un objeto realm del tipo RealmQuery
     *
     * @param id
     * @return
     */
    public Object getByID(int id) {
        return realm.where(model).equalTo("id", id).findFirst();
    }


}
