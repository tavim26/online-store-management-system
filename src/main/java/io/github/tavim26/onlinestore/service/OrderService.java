package io.github.tavim26.onlinestore.service;

import io.github.tavim26.onlinestore.dao.OrderDAO;
import io.github.tavim26.onlinestore.model.Order;
import io.github.tavim26.onlinestore.model.Product;
import io.github.tavim26.onlinestore.model.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class OrderService {


    private OrderDAO orderDAO;

    public OrderService() {
        orderDAO = new OrderDAO();
    }

    public void create(Order order) throws SQLException {
        orderDAO.create(order);
    }

    public void read(Order order) throws SQLException {
        orderDAO.read(order.getOrderId());
    }

    public void update(Order order) throws SQLException {
        orderDAO.update(order);
    }

    public void delete(Order order) throws SQLException {
        orderDAO.delete(order.getOrderId());
    }



    private int quantityValidator(int quantity)
    {
        if (quantity < 0)
        {
            return 1;
        }

        return 0;
    }

    private int priceValidator(double priece)
    {
        if(priece < 0)
        {
            return 1;
        }

        return 0;
    }

    private int dateValidator(LocalDate date)
    {
        if (date == null) {
            return 1;
        }

        if (date.isBefore(LocalDate.now()))
        {
            return 1;
        }

        return 0;
    }


    private int statusValidator(String status)
    {
        if(status == null || status.isEmpty())
        {
            return 1;
        }

        return 0;
    }

    private int addressValidator(String address)
    {
        if(address == null || address.isEmpty())
        {
            return 1;
        }
        return 0;
    }


    public int orderValidator(Order order)
    {
        UserService userBLL = new UserService();
        ProductService productBLL = new ProductService();

        User user = order.getUser();
        List<Product> products = order.getProducts();

        if(userBLL.userValidator(user) !=0)
        {
            return 1;
        }

        for(Product product : products)
        {
            if(productBLL.productValidator(product) !=0)
            {
                return 2;
            }
        }

        if(priceValidator(order.getTotalPriece())==1)
        {
            return 3;
        }

        if (quantityValidator(order.getQuantity()) == 1) {

            return 4;
        }

        if(addressValidator(order.getShippingAddress())==1)
        {
            return 5;
        }

        if(statusValidator(order.getOrderStatus())==1)
        {
            return 6;
        }

        if(dateValidator(order.getOrderDate())==1)
        {
            return 7;
        }

        return 0;
    }



}
