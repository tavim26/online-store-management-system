module com.example.onlinestoresystem {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;
    requires java.desktop;

    opens com.example.onlinestoresystem to javafx.fxml;
    exports com.example.onlinestoresystem;
}