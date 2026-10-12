package io.github.tavim26.onlinestore.ui;

import io.github.tavim26.onlinestore.model.Product;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The shopping cart of the current session. Prices here are only an estimate; the order uses database prices. */
public class Cart {

    public record Line(Product product, int quantity) {
        public BigDecimal subtotal() {
            return product.getPrice().multiply(BigDecimal.valueOf(quantity));
        }
    }

    private final Map<Integer, Line> lines = new LinkedHashMap<>();

    /** Adds to the quantity already in the cart. Returns false if that would exceed the stock. */
    public boolean add(Product product, int quantity) {
        return setQuantity(product, quantityOf(product.getId()) + quantity);
    }

    /** Sets the quantity (0 removes the product). Returns false if it exceeds the stock. */
    public boolean setQuantity(Product product, int quantity) {
        if (quantity > product.getStock()) {
            return false;
        }
        if (quantity <= 0) {
            lines.remove(product.getId());
        } else {
            lines.put(product.getId(), new Line(product, quantity));
        }
        return true;
    }

    public void remove(int productId) {
        lines.remove(productId);
    }

    public void clear() {
        lines.clear();
    }

    public int quantityOf(int productId) {
        Line line = lines.get(productId);
        return line == null ? 0 : line.quantity();
    }

    public List<Line> lines() {
        return List.copyOf(lines.values());
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }

    public int totalItems() {
        return lines.values().stream().mapToInt(Line::quantity).sum();
    }

    public BigDecimal total() {
        return lines.values().stream()
                .map(Line::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** product id -> quantity, the format expected by OrderService. */
    public Map<Integer, Integer> toOrderMap() {
        Map<Integer, Integer> order = new LinkedHashMap<>();
        lines.forEach((productId, line) -> order.put(productId, line.quantity()));
        return order;
    }
}