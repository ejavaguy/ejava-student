package ejava.examples.asyncmarket.ejb;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ejava.examples.asyncmarket.dao.AuctionItemDAO;
import ejava.examples.asyncmarket.dao.OrderDAO;
import ejava.examples.asyncmarket.dao.PersonDAO;
import ejava.examples.asyncmarket.jpa.JPAAuctionItemDAO;
import ejava.examples.asyncmarket.jpa.JPAOrderDAO;
import ejava.examples.asyncmarket.jpa.JPAPersonDAO;

public class AuctionConfig {
    @Produces
    @PersistenceContext(unitName="asyncMarket")
    public EntityManager em;
    
    @Produces
    public AuctionItemDAO auctionItemDao(EntityManager em) {
        JPAAuctionItemDAO impl = new JPAAuctionItemDAO();
        impl.setEntityManager(em);
        return impl;
    }
    
    @Produces
    public PersonDAO userDao(EntityManager em) {
        JPAPersonDAO impl = new JPAPersonDAO();
        impl.setEntityManager(em);
        return impl;
    }
    
    @Produces
    public OrderDAO orderDao(EntityManager em) {
        JPAOrderDAO impl = new JPAOrderDAO();
        impl.setEntityManager(em);
        return impl;
    }
}
