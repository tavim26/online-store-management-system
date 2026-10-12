package io.github.tavim26.onlinestore.ui.controller;

import io.github.tavim26.onlinestore.model.Product;
import io.github.tavim26.onlinestore.service.OrderService;
import io.github.tavim26.onlinestore.ui.Background;
import io.github.tavim26.onlinestore.ui.Cart;
import io.github.tavim26.onlinestore.ui.Feedback;
import io.github.tavim26.onlinestore.ui.Formats;
import io.github.tavim26.onlinestore.ui.Navigator;
import io.github.tavim26.onlinestore.ui.Session;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.util.Map;

public class CartController {

    @FXML
    private VBox linesBox;
    @FXML
    private VBox checkoutBox;
    @FXML
    private Label totalLabel;
    @FXML
    private Label balanceInfoLabel;
    @FXML
    private TextField addressField;
    @FXML
    private Button placeOrderButton;
    @FXML
    private Label messageLabel;

    private final OrderService orderService = new OrderService();

    @FXML
    private void initialize() {
        addressField.setText(Session.currentUser().getAddress());
        render();
    }

    private void render() {
        Cart cart = Session.cart();
        checkoutBox.setVisible(!cart.isEmpty());
        checkoutBox.setManaged(!cart.isEmpty());

        if (cart.isEmpty()) {
            linesBox.getChildren().setAll(new Label("Your cart is empty."));
            return;
        }
        linesBox.getChildren().setAll(cart.lines().stream().map(this::createLineRow).toList());
        updateTotals();
    }

    private void updateTotals() {
        BigDecimal total = Session.cart().total();
        BigDecimal balance = Session.currentUser().getBalance();
        totalLabel.setText(Formats.money(total));
        balanceInfoLabel.setText("Your balance: " + Formats.money(balance)
                + (balance.compareTo(total) < 0 ? " (not enough for this order)" : ""));
    }

    private Node createLineRow(Cart.Line line) {
        Product product = line.product();

        Label name = new Label(product.getName());
        name.getStyleClass().add("product-name");
        Label unitPrice = new Label(Formats.money(product.getPrice()) + " each");
        unitPrice.getStyleClass().add("subtitle");

        Label subtotal = new Label(Formats.money(line.subtotal()));
        subtotal.setMinWidth(120);
        subtotal.setAlignment(Pos.CENTER_RIGHT);

        Spinner<Integer> quantity = new Spinner<>(1, Math.max(1, product.getStock()), line.quantity());
        quantity.setPrefWidth(90);
        quantity.valueProperty().addListener((obs, oldValue, newValue) -> {
            Session.cart().setQuantity(product, newValue);
            subtotal.setText(Formats.money(product.getPrice().multiply(BigDecimal.valueOf(newValue))));
            updateTotals();
            Navigator.refreshHeader();
        });

        Button remove = new Button("Remove");
        remove.setOnAction(event -> {
            Session.cart().remove(product.getId());
            render();
            Navigator.refreshHeader();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(16, new VBox(2, name, unitPrice), spacer, quantity, subtotal, remove);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    @FXML
    private void onPlaceOrder() {
        Feedback.clear(messageLabel);

        int userId = Session.currentUser().getId();
        Map<Integer, Integer> items = Session.cart().toOrderMap();
        String address = addressField.getText();

        placeOrderButton.setDisable(true);
        Background.run(
                () -> orderService.placeOrder(userId, items, address),
                order -> {
                    Session.cart().clear();
                    render();
                    Feedback.success(messageLabel, "Order #" + order.getId() + " placed. Total paid: "
                            + Formats.money(order.getTotal()) + ".");
                    placeOrderButton.setDisable(false);
                    Navigator.reloadUser();
                },
                error -> {
                    Feedback.error(messageLabel, error);
                    placeOrderButton.setDisable(false);
                });
    }
}