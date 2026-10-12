package io.github.tavim26.onlinestore;

import io.github.tavim26.onlinestore.ui.Navigator;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        Navigator.init(stage);
        Navigator.show(Navigator.View.LOGIN);
        stage.setMinWidth(820);
        stage.setMinHeight(580);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}