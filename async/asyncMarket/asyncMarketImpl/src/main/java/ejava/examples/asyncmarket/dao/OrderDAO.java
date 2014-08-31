package ejava.examples.asyncmarket.dao;

import java.util.List;
import java.util.Map;

import ejava.examples.asyncmarket.bo.Order;

public interface OrderDAO {
    Order createOrder(Order order);
    Order getOrder(long orderId);
    Order updateOrder(Order order);
    List<Order> getOrdersforItem(long itemIdm, int index, int count);
    List<Order> getOrders(int index, int count);    
    List<Order> getOrders(
        String queryString, Map<String, Object> params, int index, int count);    
}
