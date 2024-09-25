package BusinessLogic;

public class ProductBLL {

    private int idValidator(String productID) {
        if (productID == null || productID.isEmpty()) {
            return 1;
        }
        return 0;
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

}

