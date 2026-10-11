package Database;



import Model.Review;
import Model.Product;
import Model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO extends AbstractDAO<Review> {

    @Override
    public void create(Review review) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            String query = "INSERT INTO Reviews (UserID, ProductID, ReviewText, Rating, ReviewDate) VALUES (?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, review.getUser().getUserID());
            preparedStatement.setInt(2, review.getProduct().getProductID());
            preparedStatement.setString(3, review.getReviewText());
            preparedStatement.setInt(4, review.getRating());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(review.getReviewDate()));
            preparedStatement.executeUpdate();

            // Get the generated review ID
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int reviewId = generatedKeys.getInt(1);
                review.setReviewID(reviewId);
            }
        } finally {
            closePreparedStatement(preparedStatement);
            closeConnection();
        }
    }

    @Override
    public Review read(int id) throws SQLException {
        Review review = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            String query = "SELECT * FROM Reviews WHERE ReviewID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt("UserID");
                int productId = resultSet.getInt("ProductID");
                String reviewText = resultSet.getString("ReviewText");
                int rating = resultSet.getInt("Rating");
                LocalDateTime reviewDate = resultSet.getTimestamp("ReviewDate").toLocalDateTime();

                // Fetch user and product
                User user = getUserById(userId);
                Product product = getProductById(productId);

                review = new Review(
                        id,
                        user,
                        product,
                        reviewText,
                        rating,
                        reviewDate
                );
            }
        } finally {
            closeResultSet(resultSet);
            closePreparedStatement(preparedStatement);
            closeConnection();
        }

        return review;
    }

    private User getUserById(int userId) {
        // Logic to fetch user by ID
        return null; // Placeholder
    }

    private Product getProductById(int productId) {
        // Logic to fetch product by ID
        return null; // Placeholder
    }

    @Override
    public void update(Review review) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            String query = "UPDATE Reviews SET UserID = ?, ProductID = ?, ReviewText = ?, Rating = ?, ReviewDate = ? WHERE ReviewID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, review.getUser().getUserID());
            preparedStatement.setInt(2, review.getProduct().getProductID());
            preparedStatement.setString(3, review.getReviewText());
            preparedStatement.setInt(4, review.getRating());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(review.getReviewDate()));
            preparedStatement.setInt(6, review.getReviewID());
            preparedStatement.executeUpdate();
        } finally {
            closePreparedStatement(preparedStatement);
            closeConnection();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            String query = "DELETE FROM Reviews WHERE ReviewID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } finally {
            closePreparedStatement(preparedStatement);
            closeConnection();
        }
    }

    @Override
    public List<Review> readAll() throws SQLException {
        List<Review> reviews = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            String query = "SELECT * FROM Reviews";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Review review = read(resultSet.getInt("ReviewID")); // Reuse read method
                reviews.add(review);
            }
        } finally {
            closeResultSet(resultSet);
            closePreparedStatement(preparedStatement);
            closeConnection();
        }

        return reviews;
    }
}
