package io.github.tavim26.onlinestore.service;

import io.github.tavim26.onlinestore.config.DataAccessException;
import io.github.tavim26.onlinestore.config.Database;
import io.github.tavim26.onlinestore.dao.ProductDao;
import io.github.tavim26.onlinestore.model.Product;

import java.math.BigDecimal;
import java.util.List;

public class ProductService {

    private static final BigDecimal MAX_PRICE = new BigDecimal("99999999.99"); // DECIMAL(10,2)

    private final ProductDao productDao = new ProductDao();

    public List<Product> getAll() {
        return Database.withConnection(productDao::findAll);
    }

    public Product getById(int productId) {
        return Database.withConnection(c -> productDao.findById(c, productId))
                .orElseThrow(() -> new BusinessException("Product not found."));
    }

    public Product create(Product product) {
        validate(product);
        Database.withConnection(c -> productDao.insert(c, product));
        return product;
    }

    public void update(Product product) {
        validate(product);
        if (!Database.withConnection(c -> productDao.update(c, product))) {
            throw new BusinessException("Product not found.");
        }
    }

    public void delete(int productId) {
        try {
            if (!Database.withConnection(c -> productDao.delete(c, productId))) {
                throw new BusinessException("Product not found.");
            }
        } catch (DataAccessException e) {
            if (e.isConstraintViolation()) {
                throw new BusinessException(
                        "This product appears in existing orders and cannot be deleted. Set its stock to 0 instead.");
            }
            throw e;
        }
    }

    static void validate(Product product) {
        BigDecimal price = product.getPrice();
        new Validator()
                .check(!Validator.isBlank(product.getName()) && product.getName().length() <= 100,
                        "Product name is required (max 100 characters).")
                .check(!Validator.isBlank(product.getDescription()),
                        "Product description is required.")
                .check(price != null && price.signum() >= 0 && price.scale() <= 2
                                && price.compareTo(MAX_PRICE) <= 0,
                        "Price must be between 0 and 99,999,999.99, with at most 2 decimals.")
                .check(product.getStock() >= 0,
                        "Stock cannot be negative.")
                .throwIfInvalid();
    }
}