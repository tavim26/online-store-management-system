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

public class RegisterController {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField cnpField;
    @FXML
    private TextField addressField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button registerButton;

    private final UserService userService = new UserService();

    @FXML
    private void initialize() {
        errorLabel.managedProperty().bind(errorLabel.textProperty().isNotEmpty());
    }

    @FXML
    private void onRegister() {
        errorLabel.setText("");

        // Matching passwords is a form concern, so it is checked here, not in the service
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            errorLabel.setText("Passwords do not match.");
            return;
        }

        UserService.Registration form = new UserService.Registration(
                firstNameField.getText(),
                lastNameField.getText(),
                emailField.getText(),
                passwordField.getText(),
                cnpField.getText().trim(),
                addressField.getText(),
                phoneField.getText().trim());

        registerButton.setDisable(true);
        Background.run(
                () -> userService.register(form),
                user -> {
                    Session.login(user);
                    Navigator.show(View.MAIN);
                },
                error -> {
                    errorLabel.setText(ErrorMessages.of(error));
                    registerButton.setDisable(false);
                });
    }

    @FXML
    private void onBackToLogin() {
        Navigator.show(View.LOGIN);
    }
}