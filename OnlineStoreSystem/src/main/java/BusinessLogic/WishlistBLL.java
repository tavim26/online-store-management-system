package BusinessLogic;

import Database.WishlistDAO;
import Model.Product;
import Model.User;
import Model.Wishlist;

import java.sql.SQLException;
import java.util.List;

public class WishlistBLL {


    private WishlistDAO wishlistDAO;


    public WishlistBLL() {
        wishlistDAO = new WishlistDAO();
    }

    public void create(Wishlist wishlist) throws SQLException {
        wishlistDAO.create(wishlist);
    }


    public void read(Wishlist wishlist) throws SQLException {
        wishlistDAO.read(wishlist.getWishListID());
    }

    public void update(Wishlist wishlist) throws SQLException {
        wishlistDAO.update(wishlist);
    }

    public void delete(Wishlist wishlist) throws SQLException {
        wishlistDAO.delete(wishlist.getWishListID());
    }

    public int wishListValidator(Wishlist wishlist)
    {
        UserBLL userBLL = new UserBLL();
        ProductBLL productBLL = new ProductBLL();

        User user = wishlist.getUser();
        List<Product> products = wishlist.getProducts();

        if(userBLL.userValidator(user)!=0)
        {
            return 1;
        }

        for(Product product : products)
        {
            if(productBLL.productValidator(product)!=0)
            {
                return 2;
            }
        }

        return 0;
    }
}
