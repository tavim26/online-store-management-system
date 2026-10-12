package io.github.tavim26.onlinestore.ui.controller;

import io.github.tavim26.onlinestore.model.Product;
import io.github.tavim26.onlinestore.service.ProductService;
import io.github.tavim26.onlinestore.ui.Background;
import io.github.tavim26.onlinestore.ui.Dialogs;
import io.github.tavim26.onlinestore.ui.Feedback;
import io.github.tavim26.onlinestore.ui.Inputs;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.math.BigDecimal;

public class AdminProductsController {

    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, String> nameColumn;
    @FXML
    private TableColumn<Product, BigDecimal> priceColumn;
    @FXML
    private TableColumn<Product, Integer> stockColumn;
    @FXML
    private Label formTitle;
    @FXML
    private TextField nameField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private TextField priceField;
    @FXML
    private TextField stockField;
    @FXML
    private Button saveButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Label messageLabel;

    private final ProductService productService = new ProductService();

    /** The product being edited, or null when creating a new one. */
    private Product selected;

    @FXML
    private void initialize() {
        // Lambdas instead of PropertyValueFactory: checked by the compiler and no reflection needed
        nameColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getName()));
        priceColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getPrice()));
        stockColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getStock()));

        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        productTable.setPlaceholder(new Label("No products yet."));
        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldProduct, product) -> {
            if (product != null) {
                edit(product);
            }
        });

        startNew();
        loadProducts(null);
    }

    /** Reloads the table and, if an id is given, selects that product afterwards. */
    private void loadProducts(Integer productIdToSelect) {
        Background.run(
                productService::getAll,
                products -> {
                    productTable.getItems().setAll(products);
                    if (productIdToSelect != null) {
                        products.stream()
                                .filter(p -> p.getId() == productIdToSelect)
                                .findFirst()
                                .ifPresent(p -> productTable.getSelectionModel().select(p));
                    }
                },
                error -> Feedback.error(messageLabel, error));
    }

    private void edit(Product product) {
        selected = product;
        formTitle.setText("Edit product #" + product.getId());
        nameField.setText(product.getName());
        descriptionArea.setText(product.getDescription());
        priceField.setText(product.getPrice().toPlainString());
        stockField.setText(String.valueOf(product.getStock()));
        deleteButton.setDisable(false);
    }

    private void startNew() {
        selected = null;
        productTable.getSelectionModel().clearSelection();
        formTitle.setText("New product");
        nameField.clear();
        descriptionArea.clear();
        priceField.clear();
        stockField.setText("0");
        deleteButton.setDisable(true);
    }

    @FXML
    private void onNew() {
        Feedback.clear(messageLabel);
        startNew();
    }

    @FXML
    private void onSave() {
        Feedback.clear(messageLabel);

        BigDecimal price = Inputs.decimal(priceField.getText());
        if (price == null) {
            Feedback.error(messageLabel, "Price must be a number, e.g. 149.99.");
            return;
        }
        Integer stock = Inputs.integer(stockField.getText());
        if (stock == null) {
            Feedback.error(messageLabel, "Stock must be a whole number.");
            return;
        }

        boolean isNew = selected == null;
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();
        Product product = isNew
                ? new Product(name, description, price, stock)
                : new Product(selected.getId(), name, description, price, stock);

        saveButton.setDisable(true);
        Background.run(
                () -> {
                    if (isNew) {
                        productService.create(product);
                    } else {
                        productService.update(product);
                    }
                    return product.getId();
                },
                id -> {
                    Feedback.success(messageLabel, isNew ? "Product created." : "Product saved.");
                    saveButton.setDisable(false);
                    loadProducts(id);
                },
                error -> {
                    Feedback.error(messageLabel, error);
                    saveButton.setDisable(false);
                });
    }

    @FXML
    private void onDelete() {
        if (selected == null) {
            return;
        }
        Product product = selected;
        if (!Dialogs.confirm("Delete \"" + product.getName() + "\"? This cannot be undone.")) {
            return;
        }

        Background.run(
                () -> {
                    productService.delete(product.getId());
                    return null;
                },
                ignored -> {
                    Feedback.success(messageLabel, "Product deleted.");
                    startNew();
                    loadProducts(null);
                },
                error -> Feedback.error(messageLabel, error));
    }
}