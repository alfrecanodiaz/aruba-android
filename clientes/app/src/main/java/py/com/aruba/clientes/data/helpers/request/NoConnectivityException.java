package py.com.aruba.clientes.data.helpers.request;

import java.io.IOException;

class NoConnectivityException extends IOException {

    @Override
    public String getMessage() {
        return "Por favor verifique su conexión a internet.";
        // You can send any message whatever you want from here.
    }
}
