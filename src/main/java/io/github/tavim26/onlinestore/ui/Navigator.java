package io.github.tavim26.onlinestore.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

/** Switches between full screens and between pages inside the main screen. */
public final class Navigator {

    /** Full screens that replace the whole window content. */
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

    /** Pages shown inside the main screen, below the top bar. */
    public enum Page {
        PRODUCTS("products.fxml"),
        PRODUCT_DETAILS("product-details.fxml"),
        CART("cart.fxml"),
        ORDERS("orders.fxml"),
        WISHLIST("wishlist.fxml"),
        ACCOUNT("account.fxml"),
        ADMIN_PRODUCTS("admin-products.fxml"),
        ADMIN_ORDERS("admin-orders.fxml");

        private final String fxml;

        Page(String fxml) {
            this.fxml = fxml;
        }
    }

    /** Implemented by the main screen, which hosts the pages. */
    public interface Shell {
        void setContent(Parent content);

        /** Updates the top bar from the data already in the session (fast). */
        void refreshHeader();

        /** Reloads the user from the database (e.g. after the balance changed), then updates the top bar. */
        void reloadUser();
    }

    private static final String VIEW_PATH = "/io/github/tavim26/onlinestore/view/";
    private static final String STYLESHEET = "/io/github/tavim26/onlinestore/css/app.css";

    private static Stage stage;
    private static Shell shell;

    private Navigator() {
    }

    public static void init(Stage primaryStage) {
        stage = primaryStage;
        Scene scene = new Scene(new StackPane(), 1000, 680);
        scene.getStylesheets().add(resource(STYLESHEET).toExternalForm());
        stage.setScene(scene);
    }

    public static void show(View view) {
        stage.getScene().setRoot(load(view.fxml).root());
        stage.setTitle("Online Store – " + view.title);
    }

    public static void setShell(Shell mainScreen) {
        shell = mainScreen;
    }

    /** Shows a page inside the main screen and returns its controller, so data can be passed to it. */
    public static <T> T showPage(Page page) {
        Loaded<T> loaded = load(page.fxml);
        shell.setContent(loaded.root());
        return loaded.controller();
    }

    public static void refreshHeader() {
        if (shell != null) {
            shell.refreshHeader();
        }
    }

    public static void reloadUser() {
        if (shell != null) {
            shell.reloadUser();
        }
    }

    private record Loaded<T>(Parent root, T controller) {
    }

    private static <T> Loaded<T> load(String fxmlFile) {
        FXMLLoader loader = new FXMLLoader(resource(VIEW_PATH + fxmlFile));
        try {
            Parent root = loader.load();
            return new Loaded<>(root, loader.getController());
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