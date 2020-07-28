package py.com.aruba.clientes.utils;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    /**
     * Método para devolver el tiempo actual en segundos
     *
     * @return
     */
    public static int getActualTimeInSeconds() {
        Calendar now = Calendar.getInstance();

        int resultOneWay = (now.get(Calendar.HOUR_OF_DAY) * 60 * 60) + (now.get(Calendar.MINUTE) * 60) + now.get(Calendar.SECOND);
        int result = (int) now.getTimeInMillis() / 1000;

        return result;
    }

    /**
     * Verificamos si la fecha recibida, es la fecha de hoy
     *
     * @param date
     * @return
     */
    public static boolean isSameDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        boolean isToday = isSameDay(cal, Calendar.getInstance());
        return isToday;
    }

    /**
     * Formato de fecha par enviar al backend un Date
     *
     * @param date
     * @return
     */
    public static String getDateString(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String d = sdf.format(date);
        return d;
    }

    /**
     * Formato de fecha para que los usuarios normales puedan leer mas fácil
     *
     * @param date
     * @return
     */
    public static String getDateStringReadable(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String d = sdf.format(date);
        return d;
    }

    /**
     * Formato de fecha para que los usuarios normales puedan leer mas fácil
     *
     * @param date
     * @return
     */
    public static String getDateStringReadable(String date) {
        if (date == null) return "";
        Date d = null;

        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
        try {
            d = input.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy HH:MM");
        String readable = output.format(d);
        return readable;
    }

    /**
     * <p>Checks if two dates are on the same day ignoring time.</p>
     *
     * @param date1 the first date, not altered, not null
     * @param date2 the second date, not altered, not null
     * @return true if they represent the same day
     * @throws IllegalArgumentException if either date is <code>null</code>
     */
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }

    /**
     * <p>Checks if two calendars represent the same day ignoring time.</p>
     *
     * @param cal1 the first calendar, not altered, not null
     * @param cal2 the second calendar, not altered, not null
     * @return true if they represent the same day
     * @throws IllegalArgumentException if either calendar is <code>null</code>
     */
    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public static String secondsToHour(int duration) {
        int hours = duration / 3600;
        int minutes = ((duration) % 3600) / 60; // a la duración le restamos 1 minuto porque el backend devuelve así
        String durationStr = String.format("%s:%s", pad(hours), pad(minutes));
        return durationStr;
    }

    /**
     * Agregamos un cero adelante de digitos individuales
     *
     * @param digit
     * @return
     */
    private static String pad(int digit) {
        return (digit > 9) ? String.valueOf(digit) : ("0" + digit);
    }

    public static String getWeekDay(int value) {
        String day = "";

        switch (value) {
            case 1:
                day = "Dom";
                break;
            case 2:
                day = "Lun";
                break;
            case 3:
                day = "Mar";
                break;
            case 4:
                day = "Mié";
                break;
            case 5:
                day = "Jue";
                break;
            case 6:
                day = "Vie";
                break;
            case 7:
                day = "Sáb";
                break;
        }
        return day;
    }

    public static String getMonth(int value) {
        String month = "";

        switch (value) {
            case 1:
                month = "Enero";
                break;
            case 2:
                month = "Febrero";
                break;
            case 3:
                month = "Marzo";
                break;
            case 4:
                month = "Abril";
                break;
            case 5:
                month = "Mayo";
                break;
            case 6:
                month = "Junio";
                break;
            case 7:
                month = "Julio";
                break;
            case 8:
                month = "Agosto";
                break;
            case 9:
                month = "Septiembre";
                break;
            case 10:
                month = "Octubre";
                break;
            case 11:
                month = "Noviembre";
                break;
            case 12:
                month = "Diciembre";
                break;

        }
        return month;
    }
}
