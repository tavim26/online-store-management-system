package io.github.tavim26.onlinestore.ui.controller;

import io.github.tavim26.onlinestore.service.UserService;
import io.github.tavim26.onlinestore.ui.Background;
import io.github.tavim26.onlinestore.ui.ErrorMessages;
import io.github.tavim26.onlinestore.ui.Navigator;
import io.github.tavim26.onlinestore.ui.Navigator.View;
import io.github.tavim26.onlinestore.ui.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button loginButton;

    private final UserService userService = new UserService();

    @FXML
    private void initialize() {
        // The error label takes no space while it is empty
        errorLabel.managedProperty().bind(errorLabel.textProperty().isNotEmpty());
    }

    @FXML
    private void onLogin() {
        errorLabel.setText("");
        loginButton.setDisable(true);

        String email = emailField.getText();
        String password = passwordField.getText();

        Background.run(
                () -> userService.login(email, password),
                user -> {
                    Session.login(user);
                    Navigator.show(View.MAIN);
                },
                error -> {
                    errorLabel.setText(ErrorMessages.of(error));
                    passwordField.clear();
                    loginButton.setDisable(false);
                });
    }

    @FXML
    private void onGoToRegister() {
        Navigator.show(View.REGISTER);
    }
}