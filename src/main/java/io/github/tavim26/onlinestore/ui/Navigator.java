package io.github.tavim26.onlinestore.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

/** Switches the content of the main window between screens. */
public final class Navigator {

    public enum View {
        LOGIN("login.fxml", "Sign in"),
        REGISTER("register.fxml", "Create account"),
        MAIN("main.fxml", "Store");

        private final String fxml;
        private final String title;

        View(String fxml, String title) {
            this.fxml = fxml;
            this.title = title;
        }
    }

    private static final String VIEW_PATH = "/io/github/tavim26/onlinestore/view/";
    private static final String STYLESHEET = "/io/github/tavim26/onlinestore/css/app.css";

    private static Stage stage;

    private Navigator() {
    }

    public static void init(Stage primaryStage) {
        stage = primaryStage;
        Scene scene = new Scene(new StackPane(), 1000, 680);
        scene.getStylesheets().add(resource(STYLESHEET).toExternalForm());
        stage.setScene(scene);
    }

    public static void show(View view) {
        stage.getScene().setRoot(load(view.fxml));
        stage.setTitle("Online Store – " + view.title);
    }

    /** Loads an FXML file from the view folder. Also used later to load pages inside the main screen. */
    public static Parent load(String fxmlFile) {
        try {
            return FXMLLoader.load(resource(VIEW_PATH + fxmlFile));
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot load view " + fxmlFile, e);
        }
    }

    private static URL resource(String path) {
        URL url = Navigator.class.getResource(path);
        if (url == null) {
            throw new IllegalStateException("Resource not found: " + path);
        }
        return url;
    }
}