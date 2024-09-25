package Model;

import java.time.LocalDateTime;
import java.util.List;

public class Order {

    private String orderId;
    private User user;
    private List<Product> products;
    private int quantity;
    private double totalPriece;
    private LocalDateTime orderDate;
    private String orderStatus;
    private String shippingAddress;



}
