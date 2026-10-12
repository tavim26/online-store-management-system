package io.github.tavim26.onlinestore.ui.controller;

import io.github.tavim26.onlinestore.model.User;
import io.github.tavim26.onlinestore.service.UserService;
import io.github.tavim26.onlinestore.ui.Background;
import io.github.tavim26.onlinestore.ui.ErrorMessages;
import io.github.tavim26.onlinestore.ui.Formats;
import io.github.tavim26.onlinestore.ui.Navigator;
import io.github.tavim26.onlinestore.ui.Navigator.Page;
import io.github.tavim26.onlinestore.ui.Navigator.View;
import io.github.tavim26.onlinestore.ui.Session;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.StackPane;

public class MainController implements Navigator.Shell {

    @FXML
    private Label userLabel;
    @FXML
    private Label balanceLabel;
    @FXML
    private Button cartButton;
    @FXML
    private MenuButton adminMenu;
    @FXML
    private StackPane contentPane;

    private final UserService userService = new UserService();

    @FXML
    private void initialize() {
        Navigator.setShell(this);

        // The admin menu exists only for admins
        adminMenu.setVisible(Session.isAdmin());
        adminMenu.setManaged(Session.isAdmin());

        refreshHeader();
        Navigator.showPage(Page.PRODUCTS);
    }

    @Override
    public void setContent(Parent content) {
        contentPane.getChildren().setAll(content);
    }

    @Override
    public void refreshHeader() {
        User user = Session.currentUser();
        userLabel.setText(user.getFullName() + (Session.isAdmin() ? " (admin)" : ""));
        balanceLabel.setText(Formats.money(user.getBalance()));

        int items = Session.cart().totalItems();
        cartButton.setText(items == 0 ? "Cart" : "Cart (" + items + ")");
    }

    @Override
    public void reloadUser() {
        int userId = Session.currentUser().getId();
        Background.run(
                () -> userService.getById(userId),
                user -> {
                    // The user may have signed out while this was running
                    if (Session.isLoggedIn()) {
                        Session.updateUser(user);
                        refreshHeader();
                    }
                },
                ErrorMessages::of); // only logs; the top bar keeps the last known values
    }

    @FXML
    private void onProducts() {
        Navigator.showPage(Page.PRODUCTS);
    }

    @FXML
    private void onCart() {
        Navigator.showPage(Page.CART);
    }

    @FXML
    private void onOrders() {
        Navigator.showPage(Page.ORDERS);
    }

    @FXML
    private void onWishlist() {
        Navigator.showPage(Page.WISHLIST);
    }

    @FXML
    private void onAccount() {
        Navigator.showPage(Page.ACCOUNT);
    }

    @FXML
    private void onAdminProducts() {
        Navigator.showPage(Page.ADMIN_PRODUCTS);
    }

    @FXML
    private void onAdminOrders() {
        Navigator.showPage(Page.ADMIN_ORDERS);
    }

    @FXML
    private void onLogout() {
        Session.logout();
        Navigator.show(View.LOGIN);
    }
}