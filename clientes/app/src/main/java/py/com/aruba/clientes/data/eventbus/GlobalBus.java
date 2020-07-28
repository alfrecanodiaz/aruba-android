package py.com.aruba.clientes.data.eventbus;


import org.greenrobot.eventbus.EventBus;

/**
 * Created by Gustavo on 24/01/18.
 */

public class GlobalBus {
    private static EventBus sBus;

    public static EventBus getBus() {
        if (sBus == null)
            sBus = EventBus.getDefault();
        return sBus;
    }
}

