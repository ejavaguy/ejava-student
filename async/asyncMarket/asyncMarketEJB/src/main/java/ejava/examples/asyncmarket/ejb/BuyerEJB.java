package ejava.examples.asyncmarket.ejb;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.asyncmarket.MarketException;
import ejava.examples.asyncmarket.bo.AuctionItem;
import ejava.examples.asyncmarket.bo.Bid;
import ejava.examples.asyncmarket.bo.Order;
import ejava.examples.asyncmarket.bo.Person;
import ejava.examples.asyncmarket.dao.AuctionItemDAO;
import ejava.examples.asyncmarket.dao.OrderDAO;
import ejava.examples.asyncmarket.dao.PersonDAO;
import ejava.examples.asyncmarket.jpa.JPAAuctionItemDAO;
import ejava.examples.asyncmarket.jpa.JPAOrderDAO;
import ejava.examples.asyncmarket.jpa.JPAPersonDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class BuyerEJB implements BuyerRemote, BuyerLocal {
    private static final Logger log = LoggerFactory.getLogger(BuyerEJB.class);
    
    @PersistenceContext(unitName="asyncMarket")
    private EntityManager em;
    
    private AuctionItemDAO auctionItemDAO;
    private PersonDAO userDAO;
    private OrderDAO orderDAO;
    
    @PostConstruct
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    void init() {
        log.info("*** BuyerEJB init() ***");
        log.debug("em=" + em);
        
        auctionItemDAO = new JPAAuctionItemDAO();
        ((JPAAuctionItemDAO)auctionItemDAO).setEntityManager(em);
        
        userDAO = new JPAPersonDAO();
        ((JPAPersonDAO)userDAO).setEntityManager(em);
        
        orderDAO = new JPAOrderDAO();
        ((JPAOrderDAO)orderDAO).setEntityManager(em);
    }

    public long bidProduct(long itemId, String userId, double amount)
            throws MarketException {
        try {
            log.debug("bidProduct(itemId=" + itemId +
                    ", userId=" + userId +
                    ", amount=" + amount);
            Bid bid = new Bid();
            bid.setAmount(amount);
            
            AuctionItem item = auctionItemDAO.getItem(itemId);
            log.debug("found item for bid:" + item);
            item.addBid(bid); //can fail if too low
            bid.setItem(item);
            
            Person bidder = userDAO.getPersonByUserId(userId);
            bidder.getBids().add(bid);
            bid.setBidder(bidder);
            log.debug("found bidder for bid:" + bidder);
            
            em.persist(bid);
            log.debug("added bid:" + bid);
            return bid.getId();            
        }
        catch (Exception ex) {
            log.error("error bidding product", ex);
            throw new MarketException("error bidding product:" + ex);
        }
    }

    public List<AuctionItem> getAvailableItems(int index, int count) 
        throws MarketException {
        try {
            return makeDTO(
                auctionItemDAO.getAvailableItems(index, count));
        }
        catch (Exception ex) {
            log.error("error getting available items", ex);
            throw new MarketException("error getting available items:" + ex);
        }
    }

    public AuctionItem getItem(long itemId) throws MarketException {
        try {
            return makeDTO(auctionItemDAO.getItem(itemId));    
        }
        catch (Exception ex) {
            log.error("error getting item", ex);
            throw new MarketException("error getting item:" + ex);
        }
    }

    public Order getOrder(long orderId) throws MarketException {
        try {
            log.debug("getOrder(id=" + orderId + ")");
            Order daoOrder = orderDAO.getOrder(orderId);
            Order dtoOrder = makeDTO(daoOrder);
            log.debug("daoOrder=" + daoOrder);
            log.debug("dtoOrder=" + dtoOrder);
            return dtoOrder;
        }
        catch (Exception ex) {
            log.error("error getting item", ex);
            throw new MarketException("error getting item:" + ex);
        }
    }

    public long placeOrder(long productId, String userId, double maxAmount) 
        throws MarketException {
        try {
            Order order = new Order();
            AuctionItem item = auctionItemDAO.getItem(productId);
            order.setItem(item);
            Person buyer = userDAO.getPersonByUserId(userId);
            order.setBuyer(buyer);
            order.setMaxBid(maxAmount);
            orderDAO.createOrder(order);
            return order.getId();
        }
        catch (Exception ex) {
            log.error("error placing order", ex);
            throw new MarketException("error placing order:" + ex);
        }
    }
    
    private List<AuctionItem> makeDTO(List<AuctionItem> items) {
        List<AuctionItem> dto = new ArrayList<AuctionItem>();
        for (AuctionItem item : items) {
            dto.add(makeDTO(item));
        }
        return dto;
    }
    
    private AuctionItem makeDTO(AuctionItem item) {
        AuctionItem dto = new AuctionItem(item.getId());
        dto.setVersion(item.getVersion());
        dto.setName(item.getName());
        dto.setStartDate(item.getStartDate());
        dto.setEndDate(item.getEndDate());
        dto.setMinBid(item.getMinBid());
        dto.setBids(makeDTO(item.getBids(), dto));
        dto.setWinningBid(null);
        dto.setClosed(item.isClosed());
        return dto;
    }
    
    private List<Bid> makeDTO(List<Bid> bids, AuctionItem item) {
        List<Bid> dtos = new ArrayList<Bid>();
        for (Bid bid : bids) {
            Bid dto = new Bid(bid.getId());
            dto.setAmount(bid.getAmount());
            dto.setItem(item);
            item.getBids().add(dto);
            dto.setBidder(makeDTO(bid.getBidder(),dto));
            dtos.add(dto);
        }
        return dtos;
    }
    
    private Person makeDTO(Person bidder, Bid bid) {
        Person dto = new Person(bidder.getId());
        dto.setVersion(bidder.getVersion());
        dto.setUserId(bidder.getUserId());
        return dto;
    }
    
    private Order makeDTO(Order order) {
        Order dto = new Order(order.getId());
        dto.setVersion(order.getVersion());
        dto.setMaxBid(order.getMaxBid());
        dto.setBuyer(makeDTO(order.getBuyer(), dto));
        dto.setItem(makeDTO(order.getItem(), dto));
        return dto;
    }
    
    private Person makeDTO(Person buyer, Order order) {
        Person dto = new Person(buyer.getId());
        dto.setVersion(buyer.getVersion());
        dto.setUserId(buyer.getUserId());
        order.setBuyer(dto);
        return dto;
    }
    
    private AuctionItem makeDTO(AuctionItem item, Order order) {
        AuctionItem dto = new AuctionItem(item.getId());
        dto.setVersion(item.getVersion());
        dto.setMinBid(item.getMinBid());
        dto.setStartDate(item.getStartDate());
        dto.setEndDate(item.getEndDate());
        dto.setName(item.getName());
        dto.setOwner(makeDTO(item.getOwner(), dto));
        dto.setBids(makeDTO(item.getBids(), dto));
        dto.setClosed(item.isClosed());        
        return dto;
    }
    
    private Person makeDTO(Person owner, AuctionItem item) {
        Person dto = new Person(owner.getId());
        dto.setVersion(owner.getVersion());
        dto.setUserId(owner.getUserId());        
        dto.getItems().add(item);
        item.setOwner(dto);
        return dto;
    }
}
