package io.github.tavim26.onlinestore.ui.controller;

import io.github.tavim26.onlinestore.model.PaymentCard;
import io.github.tavim26.onlinestore.model.User;
import io.github.tavim26.onlinestore.service.PaymentCardService;
import io.github.tavim26.onlinestore.service.UserService;
import io.github.tavim26.onlinestore.ui.Background;
import io.github.tavim26.onlinestore.ui.Feedback;
import io.github.tavim26.onlinestore.ui.Formats;
import io.github.tavim26.onlinestore.ui.Inputs;
import io.github.tavim26.onlinestore.ui.Navigator;
import io.github.tavim26.onlinestore.ui.Session;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AccountController {

    private static final DateTimeFormatter EXPIRY_FORMAT = DateTimeFormatter.ofPattern("MM/yy");

    // Balance and cards
    @FXML
    private Label balanceLabel;
    @FXML
    private VBox cardsBox;
    @FXML
    private TextField cardNumberField;
    @FXML
    private TextField cardExpiryField;
    @FXML
    private Button addCardButton;
    @FXML
    private ChoiceBox<PaymentCard> topUpCardChoice;
    @FXML
    private TextField topUpAmountField;
    @FXML
    private Button topUpButton;
    @FXML
    private Label cardsMessageLabel;

    // Profile
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField cnpField;
    @FXML
    private TextField addressField;
    @FXML
    private Button saveProfileButton;
    @FXML
    private Label profileMessageLabel;

    // Password
    @FXML
    private PasswordField currentPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button changePasswordButton;
    @FXML
    private Label passwordMessageLabel;

    private final UserService userService = new UserService();
    private final PaymentCardService cardService = new PaymentCardService();

    @FXML
    private void initialize() {
        // How a card is shown in the drop-down list
        topUpCardChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(PaymentCard card) {
                return card == null ? "" : card.getMaskedNumber();
            }

            @Override
            public PaymentCard fromString(String text) {
                return null; // not editable
            }
        });

        showUser(Session.currentUser());
        loadCards();
    }

    private void showUser(User user) {
        balanceLabel.setText(Formats.money(user.getBalance()));
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhone());
        cnpField.setText(user.getCnp());
        addressField.setText(user.getAddress());
    }

    private int userId() {
        return Session.currentUser().getId();
    }

    // ---------------- Cards and balance ----------------

    private void loadCards() {
        int userId = userId();
        Background.run(
                () -> cardService.getCards(userId),
                this::showCards,
                error -> Feedback.error(cardsMessageLabel, error));
    }

    private void showCards(List<PaymentCard> cards) {
        if (cards.isEmpty()) {
            Label empty = new Label("No cards saved yet.");
            empty.getStyleClass().add("subtitle");
            cardsBox.getChildren().setAll(empty);
        } else {
            cardsBox.getChildren().setAll(cards.stream().map(this::createCardRow).toList());
        }

        List<PaymentCard> usable = cards.stream().filter(card -> !card.isExpired()).toList();
        topUpCardChoice.getItems().setAll(usable);
        if (!usable.isEmpty()) {
            topUpCardChoice.setValue(usable.get(0));
        }
        topUpButton.setDisable(usable.isEmpty());
    }

    private Node createCardRow(PaymentCard card) {
        Label number = new Label(card.getMaskedNumber());
        number.getStyleClass().add("product-name");

        Label expiry = new Label((card.isExpired() ? "Expired " : "Expires ")
                + card.getExpiryDate().format(EXPIRY_FORMAT));
        expiry.getStyleClass().add(card.isExpired() ? "out-of-stock" : "subtitle");

        Button remove = new Button("Remove");
        remove.setOnAction(event -> removeCard(card));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(12, number, expiry, spacer, remove);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void removeCard(PaymentCard card) {
        int userId = userId();
        Background.run(
                () -> {
                    cardService.removeCard(userId, card.getId());
                    return null;
                },
                ignored -> {
                    Feedback.success(cardsMessageLabel, card.getMaskedNumber() + " removed.");
                    loadCards();
                },
                error -> Feedback.error(cardsMessageLabel, error));
    }

    @FXML
    private void onAddCard() {
        YearMonth expiry = Inputs.expiry(cardExpiryField.getText());
        if (expiry == null) {
            Feedback.error(cardsMessageLabel, "Expiry date must have the format MM/YY, e.g. 08/28.");
            return;
        }

        int userId = userId();
        String number = cardNumberField.getText();
        addCardButton.setDisable(true);
        Background.run(
                () -> cardService.addCard(userId, number, expiry),
                card -> {
                    Feedback.success(cardsMessageLabel, card.getMaskedNumber() + " added.");
                    cardNumberField.clear();
                    cardExpiryField.clear();
                    addCardButton.setDisable(false);
                    loadCards();
                },
                error -> {
                    Feedback.error(cardsMessageLabel, error);
                    addCardButton.setDisable(false);
                });
    }

    @FXML
    private void onTopUp() {
        PaymentCard card = topUpCardChoice.getValue();
        if (card == null) {
            Feedback.error(cardsMessageLabel, "Add a valid card first.");
            return;
        }
        BigDecimal amount = Inputs.decimal(topUpAmountField.getText());
        if (amount == null) {
            Feedback.error(cardsMessageLabel, "Enter an amount, e.g. 250 or 99.90.");
            return;
        }

        int userId = userId();
        topUpButton.setDisable(true);
        Background.run(
                () -> {
                    cardService.topUp(userId, card.getId(), amount);
                    return userService.getById(userId); // fresh balance
                },
                user -> {
                    Session.updateUser(user);
                    balanceLabel.setText(Formats.money(user.getBalance()));
                    Navigator.refreshHeader();
                    topUpAmountField.clear();
                    Feedback.success(cardsMessageLabel, Formats.money(amount) + " added to your balance.");
                    topUpButton.setDisable(false);
                },
                error -> {
                    Feedback.error(cardsMessageLabel, error);
                    topUpButton.setDisable(false);
                });
    }

    // ---------------- Profile ----------------

    @FXML
    private void onSaveProfile() {
        User current = Session.currentUser();
        User updated = new User(
                current.getId(),
                current.getRole(),
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                emailField.getText(),
                current.getPasswordHash(),
                cnpField.getText().trim(),
                addressField.getText().trim(),
                phoneField.getText().trim(),
                current.getBalance(),
                current.getCreatedAt());

        saveProfileButton.setDisable(true);
        Background.run(
                () -> {
                    userService.updateProfile(updated);
                    return userService.getById(updated.getId());
                },
                user -> {
                    Session.updateUser(user);
                    showUser(user);
                    Navigator.refreshHeader();
                    Feedback.success(profileMessageLabel, "Profile saved.");
                    saveProfileButton.setDisable(false);
                },
                error -> {
                    Feedback.error(profileMessageLabel, error);
                    saveProfileButton.setDisable(false);
                });
    }

    // ---------------- Password ----------------

    @FXML
    private void onChangePassword() {
        if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
            Feedback.error(passwordMessageLabel, "The new passwords do not match.");
            return;
        }

        int userId = userId();
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();

        changePasswordButton.setDisable(true);
        Background.run(
                () -> {
                    userService.changePassword(userId, currentPassword, newPassword);
                    return userService.getById(userId);
                },
                user -> {
                    // Keep the session in sync, otherwise a later profile save would write back the old hash
                    Session.updateUser(user);
                    currentPasswordField.clear();
                    newPasswordField.clear();
                    confirmPasswordField.clear();
                    Feedback.success(passwordMessageLabel, "Password changed.");
                    changePasswordButton.setDisable(false);
                },
                error -> {
                    Feedback.error(passwordMessageLabel, error);
                    changePasswordButton.setDisable(false);
                });
    }
}