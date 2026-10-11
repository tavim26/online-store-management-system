package io.github.tavim26.onlinestore.service;

import io.github.tavim26.onlinestore.config.Database;
import io.github.tavim26.onlinestore.dao.ProductDao;
import io.github.tavim26.onlinestore.dao.WishlistDao;
import io.github.tavim26.onlinestore.model.Product;

import java.util.List;

public class WishlistService {

    private final WishlistDao wishlistDao = new WishlistDao();
    private final ProductDao productDao = new ProductDao();

    /** Returns false if the product was already in the wishlist. */
    public boolean add(int userId, int productId) {
        return Database.inTransaction(c -> {
            if (productDao.findById(c, productId).isEmpty()) {
                throw new BusinessException("Product not found.");
            }
            return wishlistDao.add(c, userId, productId);
        });
    }

    public void remove(int userId, int productId) {
        Database.withConnection(c -> wishlistDao.remove(c, userId, productId));
    }

    public List<Product> getProducts(int userId) {
        return Database.withConnection(c -> wishlistDao.findProducts(c, userId));
    }
}