package ejava.examples.asyncmarket.ejb;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

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

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class BuyerEJB implements BuyerRemote, BuyerLocal {
    private static final Logger logger = LoggerFactory.getLogger(BuyerEJB.class);

    @Inject
    EntityManager em;
    @Inject
    private AuctionItemDAO auctionItemDAO;
    @Inject
    private PersonDAO userDAO;
    @Inject
    private OrderDAO orderDAO;
    @Inject
    private DtoMapper dtoMapper;
    
    @PostConstruct
    void init() {
        logger.info("*** BuyerEJB init() ***");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public long bidProduct(long itemId, String userId, double amount)
            throws MarketException {
        try {
            logger.debug("bidProduct(itemId={}, userId={}, amount={}", itemId, userId, amount);
            Bid bid = new Bid();
            bid.setAmount(amount);
            
            AuctionItem item = auctionItemDAO.getItem(itemId);
            logger.debug("found item for bid: {}", item);
            bid.setItem(item);
            
            Person bidder = userDAO.getPersonByUserId(userId);
            bid.setBidder(bidder);
            logger.info("found bidder for bid: {}", bidder);
            
            item.addBid(bid); //can fail if too low
            bidder.getBids().add(bid);

            auctionItemDAO.createItem(item);
            logger.info("added bid: {}", bid);
            em.flush();
            return bid.getId();            
        }
        catch (Exception ex) {
            logger.error("error bidding product", ex);
            throw new MarketException("error bidding product:" + ex);
        }
    }

    public List<AuctionItem> getAvailableItems(int index, int count) 
        throws MarketException {
        try {
            return dtoMapper.toDTO(
                auctionItemDAO.getAvailableItems(index, count));
        }
        catch (Exception ex) {
            logger.error("error getting available items", ex);
            throw new MarketException("error getting available items:" + ex);
        }
    }

    public AuctionItem getItem(long itemId) throws MarketException {
        try {
            return dtoMapper.toDTO(auctionItemDAO.getItem(itemId));    
        }
        catch (Exception ex) {
            logger.error("error getting item", ex);
            throw new MarketException("error getting item:" + ex);
        }
    }

    public Order getOrder(long orderId) throws MarketException {
        try {
            logger.debug("getOrder(id=" + orderId + ")");
            Order daoOrder = orderDAO.getOrder(orderId);
            Order dtoOrder = dtoMapper.toDTO(daoOrder);
            logger.debug("daoOrder=" + daoOrder);
            logger.debug("dtoOrder=" + dtoOrder);
            return dtoOrder;
        }
        catch (Exception ex) {
            logger.error("error getting item", ex);
            throw new MarketException("error getting item:" + ex);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
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
            logger.error("error placing order", ex);
            throw new MarketException("error placing order:" + ex);
        }
    }
}
