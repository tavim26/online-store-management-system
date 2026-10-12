package io.github.tavim26.onlinestore.service;

import io.github.tavim26.onlinestore.config.DataAccessException;
import io.github.tavim26.onlinestore.config.Database;
import io.github.tavim26.onlinestore.dao.OrderDao;
import io.github.tavim26.onlinestore.dao.ReviewDao;
import io.github.tavim26.onlinestore.model.Review;

import java.util.List;
import java.util.OptionalDouble;

public class ReviewService {

    private final ReviewDao reviewDao = new ReviewDao();
    private final OrderDao orderDao = new OrderDao();

    public Review addReview(int userId, int productId, int rating, String comment) {
        String text = Validator.isBlank(comment) ? null : comment.trim();
        new Validator()
                .check(rating >= 1 && rating <= 5, "Rating must be between 1 and 5.")
                .check(text == null || text.length() <= 1000, "Comment can have at most 1000 characters.")
                .throwIfInvalid();

        Review review = new Review(userId, productId, rating, text);
        try {
            Database.inTransaction(c -> {
                if (!orderDao.hasPurchased(c, userId, productId)) {
                    throw new BusinessException("You can only review products you have bought.");
                }
                return reviewDao.insert(c, review);
            });
        } catch (DataAccessException e) {
            if (e.isConstraintViolation()) {
                throw new BusinessException("You have already reviewed this product.");
            }
            throw e;
        }
        return review;
    }

    public List<Review> getReviews(int productId) {
        return Database.withConnection(c -> reviewDao.findByProduct(c, productId));
    }

    public OptionalDouble getAverageRating(int productId) {
        return Database.withConnection(c -> reviewDao.averageRating(c, productId));
    }
}