module io.github.tavim26.onlinestore {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens io.github.tavim26.onlinestore to javafx.fxml;
    exports io.github.tavim26.onlinestore;
}