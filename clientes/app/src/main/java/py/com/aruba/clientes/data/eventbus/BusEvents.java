package py.com.aruba.clientes.data.eventbus;

/**
 * Clase para administrar todos los eventos de eventbus
 */
public class BusEvents {

    /****************************************
     * EVENTOS DE LOS MODELOS
     ****************************************/

    // User
    public static class ModelUpdated {
        public String model;
        public ModelUpdated(String model) {
            this.model = model;
        }
    }

    public static class SchedulePage {
        public int pos;
        public SchedulePage(int pos) {
            this.pos = pos;
        }
    }

    public static class ScheduleAmount {
        public double price;
        public ScheduleAmount(double price) {
            this.price = price;
        }
    }

    public static class ScheduleSearchQuery {
        public String string;
        public ScheduleSearchQuery(String newText) {
            this.string = newText;
        }
    }

    public static class ScheduleSearchQueryServices {
        public String string;
        public ScheduleSearchQueryServices(String newText) {
            this.string = newText;
        }
    }

    public static class ProfessionalListSearchQuery {
        public String string;
        public ProfessionalListSearchQuery(String query) {
            this.string = query;
        }
    }

    public static class UIUpdate {
        public String ui;
        public UIUpdate(String ui) {
            this.ui = ui;
        }
    }

}
