package io.github.tavim26.onlinestore.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public final class Dialogs {

    private Dialogs() {
    }

    /** Shows a Yes/No dialog and returns true if the user chose Yes. */
    public static boolean confirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle("Please confirm");
        alert.setHeaderText(null);
        return alert.showAndWait().filter(ButtonType.YES::equals).isPresent();
    }
}