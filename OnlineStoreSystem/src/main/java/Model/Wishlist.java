package Model;


import java.util.List;

public class Wishlist {
    private int wishListID;
    private User user;
    private List<Product> products;


    public Wishlist(int wishListID, User user, List<Product> products) {
        this.wishListID = wishListID;
        this.user = user;
        this.products = products;
    }

    public int getWishListID() {
        return wishListID;
    }

    public void setWishListID(int wishListID) {
        this.wishListID = wishListID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}


