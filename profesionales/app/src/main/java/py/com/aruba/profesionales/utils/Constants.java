package py.com.aruba.profesionales.utils;

public class Constants {

    // SharedPreferences Global
    public static final String LOGGED = "logged";
    public static final String TOKEN = "token";
    public static final String FIREBASE_TOKEN = "firebase_token";
    public static final String DEFAULT_AVATAR = "https://develop.aruba.com.py/images/logo.png";
    public static final String AGREE_TERMS = "agree_terms";


    /**
     * Definimos el enviroment en el que se ejecuta la app
     */
    public class ENV {
        public static final String LOCALHOST = "http://192.168.0.23:80/api/";
        public static final String DEVELOP = "https://develop.aruba.com.py/api/";
        public static final String STAGING = "#";
        public static final String PRODUCTION = "https://aruba.com.py/api/";
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
        // brown
        public static final int CREATED = 1; // Cuando el backend recibió el pedido de servicios
        // aqua
        public static final int ARRIVED = 2; // Cuando el profesional llegó a la casa
        // aqua
        public static final int IN_PROGRESS = 3; // Cuando se comenzó a realizar el servicio
        // aqua
        public static final int COMPLETED = 4; // Cuando termina el servicio
        // silver
        public static final int CANCELED = 5; // Cuando se canceló por algun motivo
    }
}
