package io.github.tavim26.onlinestore.ui.controller;

import io.github.tavim26.onlinestore.model.Product;
import io.github.tavim26.onlinestore.model.Review;
import io.github.tavim26.onlinestore.service.ReviewService;
import io.github.tavim26.onlinestore.service.WishlistService;
import io.github.tavim26.onlinestore.ui.Background;
import io.github.tavim26.onlinestore.ui.Feedback;
import io.github.tavim26.onlinestore.ui.Formats;
import io.github.tavim26.onlinestore.ui.Navigator;
import io.github.tavim26.onlinestore.ui.Navigator.Page;
import io.github.tavim26.onlinestore.ui.Session;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.OptionalDouble;

public class ProductDetailsController {

    private static final DateTimeFormatter REVIEW_DATE = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);

    @FXML
    private Label nameLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label stockLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Spinner<Integer> quantitySpinner;
    @FXML
    private Button addToCartButton;
    @FXML
    private Button wishlistButton;
    @FXML
    private Label messageLabel;
    @FXML
    private Label ratingLabel;
    @FXML
    private VBox reviewList;
    @FXML
    private ChoiceBox<Integer> ratingChoice;
    @FXML
    private TextArea commentArea;
    @FXML
    private Button submitReviewButton;
    @FXML
    private Label reviewMessageLabel;

    private final ReviewService reviewService = new ReviewService();
    private final WishlistService wishlistService = new WishlistService();

    private Product product;

    private record ReviewSummary(List<Review> reviews, OptionalDouble average) {
    }

    @FXML
    private void initialize() {
        ratingChoice.getItems().setAll(5, 4, 3, 2, 1);
        ratingChoice.setValue(5);
    }

    /** Called by the products page right after this page is loaded. */
    public void setProduct(Product selected) {
        this.product = selected;

        nameLabel.setText(product.getName());
        priceLabel.setText(Formats.money(product.getPrice()));
        descriptionLabel.setText(product.getDescription());
        stockLabel.setText(product.isAvailable() ? product.getStock() + " in stock" : "Out of stock");
        stockLabel.getStyleClass().add(product.isAvailable() ? "in-stock" : "out-of-stock");

        quantitySpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Math.max(1, product.getStock()), 1));
        quantitySpinner.setDisable(!product.isAvailable());
        addToCartButton.setDisable(!product.isAvailable());

        loadReviews();
    }

    private void loadReviews() {
        int productId = product.getId();
        Background.run(
                () -> new ReviewSummary(reviewService.getReviews(productId), reviewService.getAverageRating(productId)),
                this::showReviews,
                error -> Feedback.error(ratingLabel, error));
    }

    private void showReviews(ReviewSummary summary) {
        if (summary.reviews().isEmpty()) {
            ratingLabel.setText("No reviews yet.");
            reviewList.getChildren().clear();
            return;
        }
        ratingLabel.setText(String.format(Locale.US, "%.1f / 5 from %d review(s)",
                summary.average().orElse(0), summary.reviews().size()));
        reviewList.getChildren().setAll(summary.reviews().stream().map(this::createReviewRow).toList());
    }

    private Node createReviewRow(Review review) {
        Label stars = new Label("★".repeat(review.getRating()) + "☆".repeat(5 - review.getRating()));
        stars.getStyleClass().add("stars");

        Label meta = new Label("Verified buyer · " + review.getCreatedAt().format(REVIEW_DATE));
        meta.getStyleClass().add("subtitle");

        VBox row = new VBox(4, new HBox(10, stars, meta));
        if (review.getComment() != null) {
            Label comment = new Label(review.getComment());
            comment.setWrapText(true);
            row.getChildren().add(comment);
        }
        return row;
    }

    @FXML
    private void onAddToCart() {
        int quantity = quantitySpinner.getValue();
        if (Session.cart().add(product, quantity)) {
            Feedback.success(messageLabel, quantity + " × " + product.getName() + " added to cart.");
            Navigator.refreshHeader();
        } else {
            Feedback.error(messageLabel, "Not enough stock. You already have "
                    + Session.cart().quantityOf(product.getId()) + " in your cart.");
        }
    }

    @FXML
    private void onAddToWishlist() {
        int userId = Session.currentUser().getId();
        int productId = product.getId();
        wishlistButton.setDisable(true);
        Background.run(
                () -> wishlistService.add(userId, productId),
                added -> {
                    Feedback.success(messageLabel, added ? "Added to your wishlist." : "Already in your wishlist.");
                    wishlistButton.setDisable(false);
                },
                error -> {
                    Feedback.error(messageLabel, error);
                    wishlistButton.setDisable(false);
                });
    }

    @FXML
    private void onSubmitReview() {
        int userId = Session.currentUser().getId();
        int productId = product.getId();
        int rating = ratingChoice.getValue();
        String comment = commentArea.getText();

        submitReviewButton.setDisable(true);
        Background.run(
                () -> reviewService.addReview(userId, productId, rating, comment),
                review -> {
                    Feedback.success(reviewMessageLabel, "Thank you for your review!");
                    commentArea.clear();
                    submitReviewButton.setDisable(false);
                    loadReviews();
                },
                error -> {
                    Feedback.error(reviewMessageLabel, error);
                    submitReviewButton.setDisable(false);
                });
    }

    @FXML
    private void onBack() {
        Navigator.showPage(Page.PRODUCTS);
    }
}