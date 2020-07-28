package py.com.aruba.profesionales.data.eventbus;

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
        public int amount;

        public ScheduleAmount(int amount) {
            this.amount = amount;
        }
    }

    public static class ScheduleSearchQuery {
        public String string;

        public ScheduleSearchQuery(String newText) {
            this.string = newText;
        }
    }

    public static class ProfessionalListSearchQuery {
        public String string;

        public ProfessionalListSearchQuery(String query) {
            this.string = query;
        }
    }
}
