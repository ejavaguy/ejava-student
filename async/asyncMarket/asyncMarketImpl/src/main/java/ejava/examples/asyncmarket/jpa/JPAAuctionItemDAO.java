package ejava.examples.asyncmarket.jpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import ejava.examples.asyncmarket.bo.AuctionItem;
import ejava.examples.asyncmarket.bo.Bid;
import ejava.examples.asyncmarket.dao.AuctionItemDAO;

public class JPAAuctionItemDAO implements AuctionItemDAO {
    private EntityManager em;
    
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public AuctionItem getItem(long itemId) {
        return em.find(AuctionItem.class, itemId);
    }

    public AuctionItem createItem(AuctionItem item) {
        em.persist(item);
        return item;
    }
    
    public AuctionItem updateItem(AuctionItem item) {
        return em.merge(item);
    }

    @SuppressWarnings("unchecked")
    public List<AuctionItem> getItems(int index, int count) {
        Query query = em.createNamedQuery("AsyncMarket_getAuctionItems")
                        .setFirstResult(index)
                        .setMaxResults(count);
        return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<AuctionItem> getAvailableItems(int index, int count) {
        Query query = em.createNamedQuery("AsyncMarket_getAvailableAuctionItems")
                        .setFirstResult(index)
                        .setMaxResults(count);
        return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<AuctionItem> getItems(
            String queryString, Map<String, Object> params, 
            int index, int count) {
        Query query = em.createNamedQuery(queryString)
                        .setFirstResult(index)
                        .setMaxResults(count);
        if (params != null && params.size() != 0) {
            for (String name: params.keySet()) {
                query.setParameter(name, params.get(name));
            }
        }
        return query.getResultList();
    }

    public void removeItem(long id) {
        AuctionItem item = getItem(id);
        //create a copy to prevent iterating over changing collection
        List<Bid> bids = new ArrayList<Bid>(item.getBids());
        for (Bid bid : bids) {
            bid.getBidder().getBids().remove(bid);
            item.getBids().remove(bid);
            em.remove(bid);
        }
        item.getOwner().getItems().remove(item);
        em.createQuery("delete from Order o " +
                "where o.item.id = :itemId")
          .setParameter("itemId", id)
          .executeUpdate();
        em.remove(item);
    }

    public Bid getBid(long bidId) {
        return em.find(Bid.class, bidId);
    }

    public void removeBid(Bid bid) {
        bid.getBidder().getBids().remove(bid);
        bid.getItem().getBids().remove(bid);
        em.remove(bid);
    }
}
