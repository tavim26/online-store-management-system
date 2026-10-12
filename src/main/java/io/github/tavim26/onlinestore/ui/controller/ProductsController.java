package io.github.tavim26.onlinestore.ui.controller;

import io.github.tavim26.onlinestore.model.Product;
import io.github.tavim26.onlinestore.service.ProductService;
import io.github.tavim26.onlinestore.ui.Background;
import io.github.tavim26.onlinestore.ui.Feedback;
import io.github.tavim26.onlinestore.ui.Formats;
import io.github.tavim26.onlinestore.ui.Navigator;
import io.github.tavim26.onlinestore.ui.Navigator.Page;
import io.github.tavim26.onlinestore.ui.Session;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Locale;

public class ProductsController {

    @FXML
    private TextField searchField;
    @FXML
    private CheckBox inStockOnly;
    @FXML
    private Label messageLabel;
    @FXML
    private FlowPane productGrid;

    private final ProductService productService = new ProductService();
    private List<Product> allProducts = List.of();

    @FXML
    private void initialize() {
        // Filtering happens in memory, instantly, on every key press
        searchField.textProperty().addListener((obs, oldText, newText) -> render());
        inStockOnly.selectedProperty().addListener((obs, oldValue, newValue) -> render());

        productGrid.getChildren().setAll(new Label("Loading products..."));
        Background.run(
                productService::getAll,
                products -> {
                    allProducts = products;
                    render();
                },
                error -> {
                    Label errorLabel = new Label();
                    Feedback.error(errorLabel, error);
                    productGrid.getChildren().setAll(errorLabel);
                });
    }

    private void render() {
        String query = searchField.getText().trim().toLowerCase(Locale.ROOT);

        List<Node> cards = allProducts.stream()
                .filter(p -> !inStockOnly.isSelected() || p.isAvailable())
                .filter(p -> query.isEmpty()
                        || p.getName().toLowerCase(Locale.ROOT).contains(query)
                        || p.getDescription().toLowerCase(Locale.ROOT).contains(query))
                .map(this::createCard)
                .toList();

        if (cards.isEmpty()) {
            productGrid.getChildren().setAll(new Label("No products match your search."));
        } else {
            productGrid.getChildren().setAll(cards);
        }
    }

    private Node createCard(Product product) {
        Label name = new Label(product.getName());
        name.getStyleClass().add("product-name");
        name.setWrapText(true);

        Label price = new Label(Formats.money(product.getPrice()));
        price.getStyleClass().add("product-price");

        Label stock = new Label(product.isAvailable() ? product.getStock() + " in stock" : "Out of stock");
        stock.getStyleClass().add(product.isAvailable() ? "in-stock" : "out-of-stock");

        Button details = new Button("Details");
        details.setOnAction(event -> {
            ProductDetailsController controller = Navigator.showPage(Page.PRODUCT_DETAILS);
            controller.setProduct(product);
        });

        Button addToCart = new Button("Add to cart");
        addToCart.getStyleClass().add("primary");
        addToCart.setDisable(!product.isAvailable());
        addToCart.setOnAction(event -> addToCart(product));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox card = new VBox(8, name, price, stock, spacer, new HBox(8, details, addToCart));
        card.getStyleClass().addAll("card", "product-card");
        return card;
    }

    private void addToCart(Product product) {
        if (Session.cart().add(product, 1)) {
            Feedback.success(messageLabel, product.getName() + " added to cart.");
            Navigator.refreshHeader();
        } else {
            Feedback.error(messageLabel, "All available units of " + product.getName() + " are already in your cart.");
        }
    }
}