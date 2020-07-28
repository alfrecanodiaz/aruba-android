package py.com.aruba.clientes.utils;

public class Constants {

    // SharedPreferences Global
    public static final String LOGGED = "logged";
    public static final String TOKEN = "token";
    public static final String FIREBASE_TOKEN = "firebase_token";
    public static final String DEFAULT_AVATAR = "https://develop.aruba.com.py/images/logo.png";
    public static final String AGREE_TERMS = "agree_terms";

    // Validation
    public static int MINIMAL_AMOUNT = 70000;

    // Generaro horario
    public static int TIME_BETWEEN_HOURS = 20 * 60; // 20 minutos;

    /**
     * Definimos el enviroment en el que se ejecutará la app
     */
    public class ENV {
        public static final String LOCALHOST = "http://192.168.0.6:80/api/client/mobile/";
        public static final String DEVELOP = "https://develop.aruba.com.py/api/client/mobile/";
        public static final String STAGING = "";
        public static final String PRODUCTION = "https://aruba.com.py/api/client/mobile/";
    }

    public class CLIENT_TYPE {
        public static final int MAN = 1;
        public static final int WOMAN = 2;
        public static final int KIDS = 3;
    }

    public class PAYMENT_TYPE {
        public static final int CASH = 1;
        public static final int CARD = 2;
        public static final int TRANSFER = 3;
    }

    public class APPOINTMENT_STATUS {
        // Coincidir con los status del backend
        public static final int CREATED = 1; // Cuando el backend recibió el pedido de servicios
        public static final int ARRIVED = 2; // Cuando el profesional llegó a la casa
        public static final int IN_PROGRESS = 3; // Cuando se comenzó a realizar el servicio
        public static final int COMPLETED = 4; // Cuando termina el servicio
        public static final int CANCELED = 5; // Cuando se canceló por algun motivo

        // Status locales, empezamos desde el 20, por si se agregan mas status al backend
        public static final int CREATING = 21; // Cuando se crea el appointment localmente y aun no se envia al backend
        public static final int EDITING = 22; // Cuando se van a editar los servicios de un appointment, antes de mandar al backend
        public static final int IN_CART = 23; // Cuando se completó el proceso de armar el appointment y se quedó en el carrito
    }

    public class PLACEHOLDERS{
        public static  final int AVATAR = 0;
        public static final int CIRCLE_GENERAL = 1;
        public static  final int SQUARE_GENERAL = 2;
    }
}
