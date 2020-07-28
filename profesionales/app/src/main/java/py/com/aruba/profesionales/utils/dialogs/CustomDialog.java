package py.com.aruba.profesionales.utils.dialogs;

import androidx.fragment.app.DialogFragment;

public class CustomDialog extends DialogFragment {
    public ButtonsListener listeners;

    public interface ButtonsListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    // Seteamos el listener del padre
    public void setListener(ButtonsListener buttonsListener) {
        listeners = buttonsListener;
    }
}

