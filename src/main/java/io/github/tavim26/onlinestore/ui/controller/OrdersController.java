package io.github.tavim26.onlinestore.ui.controller;

import io.github.tavim26.onlinestore.model.Order;
import io.github.tavim26.onlinestore.service.OrderService;
import io.github.tavim26.onlinestore.ui.Background;
import io.github.tavim26.onlinestore.ui.Dialogs;
import io.github.tavim26.onlinestore.ui.Feedback;
import io.github.tavim26.onlinestore.ui.Formats;
import io.github.tavim26.onlinestore.ui.Navigator;
import io.github.tavim26.onlinestore.ui.OrderCards;
import io.github.tavim26.onlinestore.ui.Session;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;

public class OrdersController {

    @FXML
    private Label messageLabel;
    @FXML
    private VBox ordersBox;

    private final OrderService orderService = new OrderService();

    @FXML
    private void initialize() {
        messageLabel.managedProperty().bind(messageLabel.textProperty().isNotEmpty());
        loadOrders();
    }

    private void loadOrders() {
        int userId = Session.currentUser().getId();
        ordersBox.getChildren().setAll(new Label("Loading orders..."));
        Background.run(
                () -> orderService.getOrdersForUser(userId),
                this::render,
                error -> Feedback.error(messageLabel, error));
    }

    private void render(List<Order> orders) {
        if (orders.isEmpty()) {
            ordersBox.getChildren().setAll(new Label("You have not placed any orders yet."));
        } else {
            ordersBox.getChildren().setAll(orders.stream().map(this::createCard).toList());
        }
    }

    private Node createCard(Order order) {
        if (!order.getStatus().isCancellable()) {
            return OrderCards.create(order, null);
        }
        Button cancel = new Button("Cancel order");
        cancel.setOnAction(event -> cancelOrder(order, cancel));
        return OrderCards.create(order, null, cancel);
    }

    private void cancelOrder(Order order, Button button) {
        boolean confirmed = Dialogs.confirm("Cancel order #" + order.getId() + "?\n"
                + Formats.money(order.getTotal()) + " will be refunded to your balance.");
        if (!confirmed) {
            return;
        }

        int userId = Session.currentUser().getId();
        button.setDisable(true);
        Background.run(
                () -> {
                    orderService.cancelOrder(userId, order.getId());
                    return null;
                },
                ignored -> {
                    Feedback.success(messageLabel, "Order #" + order.getId() + " was cancelled and refunded.");
                    loadOrders();
                    Navigator.reloadUser();
                },
                error -> {
                    Feedback.error(messageLabel, error);
                    button.setDisable(false);
                });
    }
}