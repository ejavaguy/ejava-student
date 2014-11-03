package ejava.examples.asyncmarket.jpa;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import ejava.examples.asyncmarket.bo.Order;
import ejava.examples.asyncmarket.dao.OrderDAO;

public class JPAOrderDAO implements OrderDAO {
    private EntityManager em;

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public Order createOrder(Order order) {
        em.persist(order);
        return order;
    }

    public Order getOrder(long orderId) {
        return em.find(Order.class, orderId);
    }

    @SuppressWarnings("unchecked")
    public List<Order> getOrdersforItem(long itemId, int index, int count) {
        return em.createNamedQuery("AsyncMarket_getOrdersForItem")
                        .setParameter("itemId", itemId)
                        .setFirstResult(index)
                        .setMaxResults(count)
                        .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Order> getOrders(int index, int count) {
        return em.createNamedQuery("AsyncMarket_getOrders")
                 .getResultList();
    }

    public Order updateOrder(Order order) {
        return em.merge(order);
    }

    @SuppressWarnings("unchecked")
    public List<Order> getOrders(
        String queryString, Map<String, Object> params, int index, int count) {
        Query query = em.createQuery(queryString)
                        .setFirstResult(index)
                        .setMaxResults(count);
        if (params != null) {
            for (String name: params.keySet()) {
                query.setParameter(name, params.get(name));
            }
        }
        return query.getResultList();
    }
}
