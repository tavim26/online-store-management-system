package BusinessLogic;

import Database.ProductDAO;
import Model.Product;

import java.sql.SQLException;

public class ProductBLL {



    private ProductDAO productDAO;

    public ProductBLL() {
        productDAO = new ProductDAO();
    }


    public void create(Product product) throws SQLException {
        productDAO.create(product);
    }

    public void read(Product product) throws SQLException {
        productDAO.read(product.getProductID());
    }

    public void update(Product product) throws SQLException {
        productDAO.update(product);
    }

    public void delete(Product product) throws SQLException {
        productDAO.delete(product.getProductID());
    }

    private int nameValidator(String productName) {
        if (productName == null || productName.isEmpty()) {
            return 1;
        }
        return 0;
    }

    private int descriptionValidator(String productDescription) {
        if (productDescription == null || productDescription.isEmpty()) {
            return 1;
        }
        return 0;
    }

    private int priceValidator(double productPrice) {
        if (productPrice < 0) {
            return 1;
        }
        return 0;
    }

    private int quantityValidator(int productQuantity) {
        if (productQuantity < 0) {
            return 1;
        }
        return 0;
    }


    public int productValidator(Product product) {

        if(nameValidator(product.getProductName())==1)
        {
            return 1;
        }

        if(descriptionValidator(product.getProductDescription())==1)
        {
            return 2;
        }

        if(priceValidator(product.getProductPrice())==1)
        {
            return 3;
        }

        if(quantityValidator(product.getProductQuantity())==1)
        {
            return 4;
        }

        return 0;

    }

}

