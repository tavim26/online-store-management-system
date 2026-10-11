package Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Order {

    private int orderId;
    private User user;
    private List<Product> products;
    private int quantity;
    private double totalPriece;
    private LocalDate orderDate;
    private String orderStatus;
    private String shippingAddress;


    public Order(int orderId, User user, List<Product> products, int quantity, double totalPriece, LocalDate orderDate, String orderStatus, String shippingAddress) {
        this.orderId = orderId;
        this.user = user;
        this.products = products;
        this.quantity = quantity;
        this.totalPriece = totalPriece;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.shippingAddress = shippingAddress;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPriece() {
        return totalPriece;
    }

    public void setTotalPriece(double totalPriece) {
        this.totalPriece = totalPriece;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
