package io.github.tavim26.onlinestore.ui.controller;

import io.github.tavim26.onlinestore.model.Product;
import io.github.tavim26.onlinestore.service.WishlistService;
import io.github.tavim26.onlinestore.ui.Background;
import io.github.tavim26.onlinestore.ui.Feedback;
import io.github.tavim26.onlinestore.ui.Formats;
import io.github.tavim26.onlinestore.ui.Navigator;
import io.github.tavim26.onlinestore.ui.Navigator.Page;
import io.github.tavim26.onlinestore.ui.Session;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

public class WishlistController {

    @FXML
    private Label messageLabel;
    @FXML
    private VBox itemsBox;

    private final WishlistService wishlistService = new WishlistService();

    @FXML
    private void initialize() {
        messageLabel.managedProperty().bind(messageLabel.textProperty().isNotEmpty());
        load();
    }

    private void load() {
        int userId = Session.currentUser().getId();
        Background.run(
                () -> wishlistService.getProducts(userId),
                this::render,
                error -> Feedback.error(messageLabel, error));
    }

    private void render(List<Product> products) {
        if (products.isEmpty()) {
            itemsBox.getChildren().setAll(
                    new Label("Your wishlist is empty. Use \"Add to wishlist\" on a product page."));
        } else {
            itemsBox.getChildren().setAll(products.stream().map(this::createRow).toList());
        }
    }

    private Node createRow(Product product) {
        Label name = new Label(product.getName());
        name.getStyleClass().add("product-name");

        Label price = new Label(Formats.money(product.getPrice()));
        Label stock = new Label(product.isAvailable() ? "In stock" : "Out of stock");
        stock.getStyleClass().add(product.isAvailable() ? "in-stock" : "out-of-stock");

        Button details = new Button("Details");
        details.setOnAction(event -> {
            ProductDetailsController controller = Navigator.showPage(Page.PRODUCT_DETAILS);
            controller.setProduct(product);
        });

        Button addToCart = new Button("Add to cart");
        addToCart.getStyleClass().add("primary");
        addToCart.setDisable(!product.isAvailable());
        addToCart.setOnAction(event -> {
            if (Session.cart().add(product, 1)) {
                Feedback.success(messageLabel, product.getName() + " added to cart.");
                Navigator.refreshHeader();
            } else {
                Feedback.error(messageLabel, "All available units of " + product.getName() + " are already in your cart.");
            }
        });

        Button remove = new Button("Remove");
        remove.setOnAction(event -> remove(product));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(10, new VBox(2, name, new HBox(10, price, stock)), spacer, details, addToCart, remove);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void remove(Product product) {
        int userId = Session.currentUser().getId();
        Background.run(
                () -> {
                    wishlistService.remove(userId, product.getId());
                    return null;
                },
                ignored -> {
                    Feedback.success(messageLabel, product.getName() + " removed from your wishlist.");
                    load();
                },
                error -> Feedback.error(messageLabel, error));
    }
}