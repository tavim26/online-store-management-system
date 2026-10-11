package io.github.tavim26.onlinestore.ui.controller;

import io.github.tavim26.onlinestore.model.User;
import io.github.tavim26.onlinestore.ui.Formats;
import io.github.tavim26.onlinestore.ui.Navigator;
import io.github.tavim26.onlinestore.ui.Navigator.View;
import io.github.tavim26.onlinestore.ui.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class MainController {

    @FXML
    private Label userLabel;
    @FXML
    private Label balanceLabel;
    @FXML
    private Label welcomeLabel;
    @FXML
    private StackPane contentPane;

    @FXML
    private void initialize() {
        User user = Session.currentUser();
        userLabel.setText(user.getFullName() + (Session.isAdmin() ? " (admin)" : ""));
        balanceLabel.setText(Formats.money(user.getBalance()));
        welcomeLabel.setText("Welcome, " + user.getFirstName() + "!");
    }

    @FXML
    private void onLogout() {
        Session.logout();
        Navigator.show(View.LOGIN);
    }
}