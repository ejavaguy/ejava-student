package ejava.examples.asyncmarket.ejb;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.asyncmarket.MarketException;
import ejava.examples.asyncmarket.bo.AuctionItem;
import ejava.examples.asyncmarket.bo.Bid;
import ejava.examples.asyncmarket.bo.Order;
import ejava.examples.asyncmarket.dao.AuctionItemDAO;
import ejava.examples.asyncmarket.dao.OrderDAO;
import ejava.examples.asyncmarket.jpa.JPAAuctionItemDAO;
import ejava.examples.asyncmarket.jpa.JPAOrderDAO;


/**
 * This class will listen for market events and cause further bidding to 
 * occur. Note that this is mostly a technology demonstration and not
 * a great architectural demonstration. It would be best to restrict
 * this class to only the async/JMS interface and move all detailed
 * processing and business logic to lower level classes. Architecturally,
 * MDBs should be restricted to just interface adaption. If you place 
 * too much business logic here it becomes harder to test and reuse.
 *  
 */
@MessageDriven(activationConfig={
		/* I placed the first few in the ejb-jar.xml DD
		 * since they are configuration options that,
		 * when they change, do not impact the code.
        @ActivationConfigProperty(
                propertyName="destinationType",
                propertyValue="javax.jms.Topic"),            
        @ActivationConfigProperty(
                propertyName="destination",
                propertyValue="java:/topic/ejava/examples/asyncMarket/topic1"),            
        @ActivationConfigProperty(
                propertyName="messageSelector",
                propertyValue="JMSType in ('forSale', 'saleUpdate')"),
        */
		//This one, however, would impact the code if it were changed
        @ActivationConfigProperty(
                propertyName="acknowledgeMode",
                propertyValue="Auto-acknowledge")            
})
public class BuyerMDB implements MessageListener {
    private static final Log log = LogFactory.getLog(BuyerMDB.class);
    
    @EJB
    private BuyerLocal buyer;
    @EJB
    private AuctionMgmtLocal auctionMgmt;     
    @PersistenceContext(unitName="asyncMarket")
    private EntityManager em;
    
    private AuctionItemDAO auctionItemDAO;
    private OrderDAO orderDAO;
    
    @Resource
    private MessageDrivenContext ctx;
    
    @PostConstruct
    public void init() {
        log.info("*** BuyerMDB init() ***");
        log.debug("ctx=" + ctx);
        log.debug("buyer=" + buyer);
        log.debug("auctionMgmt=" + auctionMgmt);
        log.debug("em=" + em);
        
        orderDAO = new JPAOrderDAO();
        ((JPAOrderDAO)orderDAO).setEntityManager(em);
        
        auctionItemDAO = new JPAAuctionItemDAO();
        ((JPAAuctionItemDAO)auctionItemDAO).setEntityManager(em);        
    }

    @PermitAll
    public void onMessage(Message message) {
        try {
            log.debug("onMessage:" + message.getJMSMessageID());
            MapMessage auctionMsg = (MapMessage)message;
            long itemId = auctionMsg.getLong("id");
            processAuctionItem(itemId);
        }
        catch (Exception ex) {
            log.error("error processing message", ex);
        }
    }
    
    protected void processAuctionItem(long itemId) {
        int index=0;
        List<Order> orders = null;
        do {
            orders = orderDAO.getOrdersforItem(itemId, index, 10);
            for (Order order: orders) {
                processOrder(order);
            }
            index += orders.size();            
        } while (orders.size() > 0);        
    }

    protected void processOrder(Order order) {
        log.debug("processing order:" + order);
        try {
            AuctionItem item = order.getItem();
            Bid highestBid = item.getHighestBid();
            if (highestBid == null) {
                if (item.getMinBid() < order.getMaxBid()) {
                    buyer.bidProduct(item.getId(), 
                                     order.getBuyer().getUserId(), 
                                     item.getMinBid());
                    log.debug("placed initial bid for order:" + order);
                }
            }
            else if (highestBid.getAmount() < order.getMaxBid()
            		// add don't bid against ourself
            		&& item.getHighestBid().getBidder().getId() !=
            		   order.getBuyer().getId()){
                buyer.bidProduct(item.getId(), 
                                 order.getBuyer().getUserId(), 
                                 item.getHighestBid().getAmount() + 1.00);
                log.debug("placed new bid for order:" + order);
            }
        }
        catch (MarketException ex) {
            log.error("error processing order:" + order, ex);
        }
    }
}
