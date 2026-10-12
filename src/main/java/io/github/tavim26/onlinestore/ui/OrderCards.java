package io.github.tavim26.onlinestore.ui;

import io.github.tavim26.onlinestore.model.Order;
import io.github.tavim26.onlinestore.model.OrderItem;
import io.github.tavim26.onlinestore.model.OrderStatus;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Builds the card that shows one order. Used by "My orders" and by the admin orders page. */
public final class OrderCards {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", Locale.ENGLISH);

    private OrderCards() {
    }

    /**
     * @param subtitle extra text shown after the date (e.g. the customer name), or null
     * @param actions  buttons shown at the bottom of the card
     */
    public static VBox create(Order order, String subtitle, Node... actions) {
        Label title = new Label("Order #" + order.getId());
        title.getStyleClass().add("product-name");

        Label total = new Label(Formats.money(order.getTotal()));
        total.getStyleClass().add("product-price");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(10, title, statusBadge(order.getStatus()), spacer, total);
        header.setAlignment(Pos.CENTER_LEFT);

        Label date = new Label(order.getOrderDate().format(DATE) + (subtitle == null ? "" : " · " + subtitle));
        date.getStyleClass().add("subtitle");

        VBox items = new VBox(4);
        for (OrderItem item : order.getItems()) {
            items.getChildren().add(new Label(item.getQuantity() + " × " + item.getProduct().getName()
                    + "   " + Formats.money(item.getSubtotal())));
        }

        Label address = new Label("Ship to: " + order.getShippingAddress());
        address.getStyleClass().add("subtitle");
        address.setWrapText(true);

        VBox card = new VBox(8, header, date, items, address);
        card.getStyleClass().addAll("card", "order-card");
        if (actions.length > 0) {
            card.getChildren().add(new HBox(8, actions));
        }
        return card;
    }

    public static Label statusBadge(OrderStatus status) {
        Label badge = new Label(status.name());
        badge.getStyleClass().addAll("status", "status-" + status.name().toLowerCase(Locale.ROOT));
        return badge;
    }
}