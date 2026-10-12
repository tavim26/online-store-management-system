package io.github.tavim26.onlinestore.ui.controller;

import io.github.tavim26.onlinestore.model.Order;
import io.github.tavim26.onlinestore.model.OrderStatus;
import io.github.tavim26.onlinestore.model.User;
import io.github.tavim26.onlinestore.service.OrderService;
import io.github.tavim26.onlinestore.service.UserService;
import io.github.tavim26.onlinestore.ui.Background;
import io.github.tavim26.onlinestore.ui.Feedback;
import io.github.tavim26.onlinestore.ui.OrderCards;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminOrdersController {

    private static final String ALL = "ALL";

    @FXML
    private ChoiceBox<String> statusFilter;
    @FXML
    private Label messageLabel;
    @FXML
    private VBox ordersBox;

    private final OrderService orderService = new OrderService();
    private final UserService userService = new UserService();

    private List<Order> orders = List.of();
    private Map<Integer, String> customerNames = Map.of();

    private record Data(List<Order> orders, Map<Integer, String> customerNames) {
    }

    @FXML
    private void initialize() {
        statusFilter.getItems().add(ALL);
        for (OrderStatus status : OrderStatus.values()) {
            statusFilter.getItems().add(status.name());
        }
        statusFilter.setValue(ALL);
        statusFilter.valueProperty().addListener((obs, oldValue, newValue) -> render());

        load();
    }

    private void load() {
        ordersBox.getChildren().setAll(new Label("Loading orders..."));
        Background.run(
                () -> new Data(
                        orderService.getAllOrders(),
                        userService.getAll().stream().collect(Collectors.toMap(User::getId, User::getFullName))),
                data -> {
                    orders = data.orders();
                    customerNames = data.customerNames();
                    render();
                },
                error -> Feedback.error(messageLabel, error));
    }

    private void render() {
        String filter = statusFilter.getValue();
        List<Node> cards = orders.stream()
                .filter(order -> ALL.equals(filter) || order.getStatus().name().equals(filter))
                .map(this::createCard)
                .toList();

        if (cards.isEmpty()) {
            ordersBox.getChildren().setAll(new Label("No orders."));
        } else {
            ordersBox.getChildren().setAll(cards);
        }
    }

    private Node createCard(Order order) {
        String customer = "Customer: " + customerNames.getOrDefault(order.getUserId(), "User #" + order.getUserId());

        OrderStatus next = order.getStatus().next();
        if (next == null) {
            return OrderCards.create(order, customer);
        }

        Button advance = new Button("Mark as " + next.name().toLowerCase(Locale.ROOT));
        advance.getStyleClass().add("primary");
        advance.setOnAction(event -> advance(order, advance));
        return OrderCards.create(order, customer, advance);
    }

    private void advance(Order order, Button button) {
        button.setDisable(true);
        Background.run(
                () -> orderService.advanceStatus(order.getId()),
                status -> {
                    Feedback.success(messageLabel, "Order #" + order.getId() + " is now " + status + ".");
                    load();
                },
                error -> {
                    Feedback.error(messageLabel, error);
                    button.setDisable(false);
                });
    }
}