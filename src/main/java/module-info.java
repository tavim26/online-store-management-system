module io.github.tavim26.onlinestore {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;

    opens io.github.tavim26.onlinestore.ui.controller to javafx.fxml;
    exports io.github.tavim26.onlinestore;
}