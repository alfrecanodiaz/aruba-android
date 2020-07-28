package py.com.aruba.clientes.utils;

import android.text.TextUtils;
import android.widget.EditText;

public class UtilsForm {

    /**
     * Método para validar el campo email
     *
     * @param view
     * @param text
     * @return
     */
    public static boolean isEmailValid(EditText view, String text) {
        // Si esta vacio
        if (TextUtils.isEmpty(text.trim())) {
            view.setError("Este campo no puede estar vacio");
            view.requestFocus();
            return false;
        }

        // Si tiene menos de 3 caracteres
        if (text.length() < 3) {
            view.setError("Este campo debe tener más de 3 carácteres");
            view.requestFocus();
            return false;
        }

        // Si no es un email válido
        boolean valor = android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches();
        if (!valor) {
            view.setError("Por favor ingrese un email válido");
            view.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Método para validar el campo password
     *
     * @param view
     * @param text
     * @return
     */
    public static boolean isPasswordValid(EditText view, String text) {
        // Si esta vacio
        if (TextUtils.isEmpty(text.trim())) {
            view.setError("Este campo no puede estar vacio");
            view.requestFocus();
            return false;
        }

        // Si tiene menos de 3 caracteres
        if (text.length() < 6) {
            view.setError("Este campo debe tener 6 o más carácteres");
            view.requestFocus();
            return false;
        }


        return true;
    }

    /**
     * Método para validar el campo texto
     *
     * @param view
     * @param text
     * @return
     */
    public static boolean isTextValid(EditText view, String text) {
        // Si esta vacio
        if (TextUtils.isEmpty(text.trim())) {
            view.setError("Este campo no puede estar vacio");
            view.requestFocus();
            return false;
        }

        // Si tiene menos de 3 caracteres
        if (text.length() < 2) {
            view.setError("Este campo debe tener 2 o más carácteres");
            view.requestFocus();
            return false;
        }


        return true;
    }

    /**
     * Método para validar el campo texto
     *
     * @param view
     * @param text
     * @return
     */
    public static boolean isTextValidUnique(EditText view, String text) {
        // Si esta vacio
        if (TextUtils.isEmpty(text)) {
            view.setError("Este campo no puede estar vacio");
            view.requestFocus();
            return false;
        }

        // Si tiene menos de 3 caracteres
        if (text.length() < 3) {
            view.setError("Este campo debe tener más de 3 carácteres");
            view.requestFocus();
            return false;
        }

        // Verificar que no contenga espacios vacios
        if (text.contains(" ") && (!text.startsWith(" ") && !text.endsWith(" "))) {
            view.setError("No puede contener espacios vacios");
            view.requestFocus();
            return false;
        }


        return true;
    }
}
