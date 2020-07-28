package py.com.aruba.profesionales.utils;

import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    /**
     * Método para devolver el tiempo actual en segundos
     * @return
     */
    public static int getActualTimeInSeconds(){
        Calendar now = Calendar.getInstance();

        int resultOneWay = (now.get(Calendar.HOUR_OF_DAY) * 60 * 60) + (now.get(Calendar.MINUTE) * 60) + now.get(Calendar.SECOND);
        int result = (int) now.getTimeInMillis() / 1000;

        return result;
    }


    /**
     * Verificamos si la fecha recibida, es la fecha de hoy
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
     * @param date
     * @return
     */
    public static String getDateString(Date date) {
        if(date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String d = sdf.format(date);
        return d;
    }

    /**
     * Formato de fecha para que los usuarios normales puedan leer mas fácil
     * @param date
     * @return
     */
    public static String getDateStringReadable(Date date) {
        if(date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String d = sdf.format(date);
        return d;
    }

    /**
     * <p>Checks if two dates are on the same day ignoring time.</p>
     * @param date1  the first date, not altered, not null
     * @param date2  the second date, not altered, not null
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
     * @param cal1  the first calendar, not altered, not null
     * @param cal2  the second calendar, not altered, not null
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
}
