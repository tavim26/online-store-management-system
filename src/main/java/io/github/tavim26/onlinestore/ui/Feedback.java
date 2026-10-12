package io.github.tavim26.onlinestore.ui;

import javafx.scene.control.Label;

/** Shows green (success) or red (error) messages in a label. */
public final class Feedback {

    private Feedback() {
    }

    public static void success(Label label, String text) {
        set(label, text, "success");
    }

    public static void error(Label label, String text) {
        set(label, text, "error");
    }

    public static void error(Label label, Throwable error) {
        error(label, ErrorMessages.of(error));
    }

    public static void clear(Label label) {
        label.setText("");
    }

    private static void set(Label label, String text, String styleClass) {
        label.getStyleClass().removeAll("success", "error");
        label.getStyleClass().add(styleClass);
        label.setText(text);
    }
}