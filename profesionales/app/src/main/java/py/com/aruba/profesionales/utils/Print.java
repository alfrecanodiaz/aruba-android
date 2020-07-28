package py.com.aruba.profesionales.utils;

import com.elvishew.xlog.XLog;
import com.google.gson.JsonObject;

public class Print {

    /**
     * Método para imprimir los logs
     *
     * @param tag
     * @param msg
     */
    public static void d(String tag, String msg) {
        XLog
                .tag(tag)
                .st(5)  // Enable stack trace info with depth 3
                .b()
                .d(msg);
    }

    /**
     * Método para imprimir los logs
     *
     * @param tag
     * @param msg
     */
    public static void d(String tag, Object msg) {
        XLog
                .tag(tag)
                .st(5)  // Enable stack trace info with depth 3
                .b()
                .d(msg);
    }

    /**
     * Método para imprimir los logs
     *
     * @param tag
     * @param msg
     */
    public static void d(String tag, String msg, Throwable t) {
        XLog
                .tag(tag)
                .st(5)  // Enable stack trace info with depth 3
                .b()
                .d(msg, t);
    }

    /**
     * Método para imprimir los logs
     *
     * @param tag
     * @param msg
     */
    public static void d(String tag, JsonObject msg) {
        XLog
                .tag(tag)
                .st(5)  // Enable stack trace info with depth 3
                .b()
                .d(msg);
    }

    /**
     * Método para imprimir los logs
     *
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg, Throwable t) {
        XLog
                .tag(tag)
                .st(5)  // Enable stack trace info with depth 3
                .b()
                .e(msg, t);
    }

    /**
     * Método para imprimir los logs
     *
     * @param tag
     * @param msg
     */
    public static void e(String tag, JsonObject msg) {
        XLog
                .tag(tag)
                .st(5)  // Enable stack trace info with depth 3
                .b()
                .e(msg.toString());
    }

    /**
     * Método para imprimir los logs
     *
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg) {
        XLog
                .tag(tag)
                .st(5)  // Enable stack trace info with depth 3
                .b()
                .e(msg);
    }

    /**
     * Método para imprimir los logs
     *
     * @param tag
     * @param msg
     */
    public static void e(String tag, Object msg) {
        XLog
                .tag(tag)
                .st(5)  // Enable stack trace info with depth 3
                .b()
                .e(msg.toString());
    }

}
