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
            throws ResourceNotFoundException, InvalidRequestException {
        logger.debug("bidProduct(itemId={}, userId={}, amount={}", itemId, userId, amount);
        
        AuctionItem item = auctionItemDAO.getItem(itemId);
        if (item==null) {
            throw new ResourceNotFoundException("itemId[%d] not found", itemId);            
        }
        logger.debug("found item for bid: {}", item);
        
        Person bidder = userDAO.getPersonByUserId(userId);
        if (bidder==null) {
            throw new ResourceNotFoundException("bidderId[%d] not found", userId);                        
        }
        logger.info("found bidder for bid: {}", bidder);
        
        
        try {
            Bid bid = new Bid();
            bid.setAmount(amount);
            bid.setItem(item);
            item.addBid(bid); //can fail if too low
            bid.setBidder(bidder);
            bidder.getBids().add(bid);

            auctionItemDAO.createItem(item);
            logger.debug("added bid: {}", bid);
            return bid.getId();            
        } catch (IllegalArgumentException ex) {
            throw new InvalidRequestException("invalid bid:%s", ex);
        } catch (Exception ex) {
            logger.error("error bidding product", ex);
            throw new InternalErrorException("error bidding product:%s", ex);
        }
    }

    public List<AuctionItem> getAvailableItems(int index, int count) {
        try {
            return dtoMapper.toDTO(
                auctionItemDAO.getAvailableItems(index, count));
        }
        catch (Exception ex) {
            logger.error("error getting available items", ex);
            throw new InternalErrorException("error getting available items: %s", ex);
        }
    }

    public AuctionItem getItem(long itemId) throws ResourceNotFoundException {
        AuctionItem item = auctionItemDAO.getItem(itemId);
        if (item==null) {
            throw new ResourceNotFoundException("itemId[%d] not found", itemId);
        }
        
        try {
            return dtoMapper.toDTO(item);    
        }
        catch (Exception ex) {
            logger.error("error getting item", ex);
            throw new InternalErrorException("error getting item: %s", ex);
        }
    }

    public Order getOrder(long orderId) throws ResourceNotFoundException {
        logger.debug("getOrder(id={})", orderId);
        Order daoOrder = orderDAO.getOrder(orderId);
        if (daoOrder==null) {
            throw new ResourceNotFoundException("orderId[%d] not found", orderId);
        }
        logger.debug("daoOrder={}", daoOrder);

        try {
            Order dtoOrder = dtoMapper.toDTO(daoOrder);
            logger.debug("dtoOrder={}", dtoOrder);
            return dtoOrder;
        }
        catch (Exception ex) {
            logger.error("error getting item", ex);
            throw new InternalErrorException("error getting order[%d]:%s", orderId, ex);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public long placeOrder(long productId, String userId, double maxAmount) 
        throws ResourceNotFoundException, InvalidRequestException {
        AuctionItem item = auctionItemDAO.getItem(productId);
        if (item==null) {
            throw new ResourceNotFoundException("itemId[%d] not found", productId);
        }
        
        Person buyer = userDAO.getPersonByUserId(userId);
        if (buyer==null) {
            throw new ResourceNotFoundException("userId[%d] not found", userId);
        }
        
        try {
            Order order = new Order();
            order.setItem(item);
            order.setBuyer(buyer);
            order.setMaxBid(maxAmount);
            orderDAO.createOrder(order);
            return order.getId();
        }
        catch (Exception ex) {
            logger.error("error placing order", ex);
            throw new InternalErrorException("error placing order: %s", ex);
        }
    }
}
