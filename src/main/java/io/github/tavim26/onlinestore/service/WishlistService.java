package io.github.tavim26.onlinestore.service;

import io.github.tavim26.onlinestore.dao.WishlistDao;
import io.github.tavim26.onlinestore.model.Product;
import io.github.tavim26.onlinestore.model.User;
import io.github.tavim26.onlinestore.model.Wishlist;

import java.sql.SQLException;
import java.util.List;

public class WishlistService {


    private WishlistDao wishlistDAO;


    public WishlistService() {
        wishlistDAO = new WishlistDao();
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
        UserService userBLL = new UserService();
        ProductService productBLL = new ProductService();

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
