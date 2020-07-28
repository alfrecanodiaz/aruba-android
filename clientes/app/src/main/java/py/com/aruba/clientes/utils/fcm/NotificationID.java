package py.com.aruba.clientes.utils.fcm;

import java.util.Random;

public class NotificationID {

    public static int getID() {
        Random r = new Random();
        int random = r.nextInt(500 - 1) + 1;
        return random;
    }
}
