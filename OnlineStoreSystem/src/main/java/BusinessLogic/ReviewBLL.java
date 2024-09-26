package BusinessLogic;

import Database.ReviewDAO;
import Model.Product;
import Model.Review;
import Model.User;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class ReviewBLL {


    private ReviewDAO reviewDAO;

    public ReviewBLL() {
        reviewDAO = new ReviewDAO();
    }


    public void create(Review review) throws SQLException {
        reviewDAO.create(review);
    }

    public void read(Review review) throws SQLException {
        reviewDAO.read(review.getReviewID());
    }


    public void update(Review review) throws SQLException {
        reviewDAO.update(review);
    }

    public void delete(Review review) throws SQLException {
        reviewDAO.delete(review.getReviewID());
    }



    private int ratingValidator(int rating)
    {
        if (rating < 0 || rating > 10)
        {
            return 1;
        }
        return 0;
    }

    private int dateValidator(LocalDateTime date)
    {
        if(date == null || date.isBefore(LocalDateTime.now()))
        {
            return 1;
        }
        return 0;
    }


    public int reviewValidator(Review review) {
        UserBLL userBLL = new UserBLL();
        ProductBLL productBLL = new ProductBLL();

        User user = review.getUser();
        Product product = review.getProduct();


        if (userBLL.userValidator(user) != 0) {
            return 1;
        }

        if (productBLL.productValidator(product) != 0) {
            return 2;
        }

        if (ratingValidator(review.getRating()) != 0) {
            return 3;
        }

        if (dateValidator(review.getReviewDate()) != 0) {
            return 4;
        }

        return 0;
    }

}
