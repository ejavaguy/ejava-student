package ejava.examples.asyncmarket.ejb;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.asyncmarket.MarketException;
import ejava.examples.asyncmarket.bo.AuctionItem;
import ejava.examples.asyncmarket.bo.Bid;
import ejava.examples.asyncmarket.bo.Person;
import ejava.examples.asyncmarket.dao.AuctionItemDAO;
import ejava.examples.asyncmarket.dao.PersonDAO;
import ejava.examples.asyncmarket.jpa.JPAAuctionItemDAO;
import ejava.examples.asyncmarket.jpa.JPAPersonDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class SellerEJB
    implements SellerLocal, SellerRemote {
    Log log = LogFactory.getLog(SellerEJB.class);
    
    @Resource(lookup="java:/JmsXA")
    private ConnectionFactory connFactory;
    @Resource(lookup="java:/topic/ejava/examples/asyncMarket/topic1", type=Topic.class)
    private Destination sellTopic;
    
    @Resource
    private TimerService timerService;
    @Resource
    private SessionContext ctx;
    @PersistenceContext(unitName="asyncMarket")
    private EntityManager em;
    
    private PersonDAO sellerDAO;
    private AuctionItemDAO auctionItemDAO;
    
    @PostConstruct
    public void init() {
        log.info("******************* SellerEJB Created ******************");
        log.debug("ctx=" + ctx);
        log.debug("connFactory=" + connFactory);
        log.debug("sellTopic=" + sellTopic);
        log.debug("em=" + em);
        log.debug("timerService=" + timerService);

        sellerDAO = new JPAPersonDAO();
        ((JPAPersonDAO)sellerDAO).setEntityManager(em);
        
        auctionItemDAO = new JPAAuctionItemDAO();
        ((JPAAuctionItemDAO)auctionItemDAO).setEntityManager(em);        
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public long sellProduct(String sellerId, AuctionItem item) 
        throws MarketException {
        log.debug("sellProduct(sellerId=" + sellerId + ",item=" + item + ")");
        
        Connection connection = null;
        Session session = null;
        Person seller = null;
        try {
            connection = connFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                
            seller = sellerDAO.getPersonByUserId(sellerId);
            seller.getItems().add(item);
            item.setOwner(seller);
            auctionItemDAO.createItem(item);
            
            publishForSale(session, item);
            timerService.createTimer(item.getEndDate(), new Long(item.getId()));
            return item.getId();
        }
        catch (JMSException ex) {
            log.error("error publishing sell", ex);
            ctx.setRollbackOnly();
            throw new EJBException("error publishing sell");
        }
        catch (NoResultException ex) {
            log.error("error locating information for sale, seller=" + seller, 
                    ex);
            ctx.setRollbackOnly();
            throw new MarketException("error locating information for sale, " +
                    "seller=" + seller + ":" + ex);
        }
        catch (Exception ex) {
            log.error("error selling product", ex);
            ctx.setRollbackOnly();
            throw new MarketException("error selling product:" + ex);
        }
        finally {
            try {
                if (session != null)    { session.close(); }
                if (connection != null) { connection.close(); }
            } catch (JMSException ex) {
                log.error("unable to close resources", ex);
            }
        }
    }    
    
    protected void publishForSale(Session session, AuctionItem item)
        throws JMSException {
        MessageProducer producer = null;
        try {
            producer = session.createProducer(sellTopic);
            MapMessage message = session.createMapMessage();
            message.setJMSType("forSale");
            message.setLong("id", item.getId());
            message.setString("name", item.getName());
            message.setString("seller", item.getOwner().getUserId());
            message.setLong("startDate", item.getStartDate().getTime());
            message.setLong("endDate", item.getEndDate().getTime());
            message.setDouble("minBid", item.getMinBid());
            message.setDouble("bids", item.getBids().size());
            message.setDouble("highestBid", 
                    (item.getHighestBid() == null ? 0.00 :
                        item.getHighestBid().getAmount()));            
            producer.send(message);
            log.debug("sent=" + message);
        }
        finally {
            if (producer != null)   { producer.close(); }
        }
    }


    public AuctionItem getItem(long id) throws MarketException {
        try {
            AuctionItem item = auctionItemDAO.getItem(id);
            AuctionItem dto = null;
            if (item != null) {
                dto = makeDTO(item);
                log.debug("dao item=" + item);
                log.debug("dto item=" + dto);
            }
            return dto;
        }
        catch (Exception ex) {
            log.error("error getting auction item", ex);
            throw new MarketException("error getting auction item" + ex);
        }
    }
    
    protected AuctionItem makeDTO(AuctionItem item) {
        AuctionItem dto = new AuctionItem(item.getId());
        dto.setStartDate(item.getStartDate());
        dto.setEndDate(item.getEndDate());
        dto.setMinBid(item.getMinBid());
        dto.setName(item.getName());
        dto.setProductId(item.getProductId());
        dto.setVersion(item.getVersion());
        dto.setBids(makeDTO(item.getBids(), dto));
        dto.setClosed(item.isClosed());
        dto.setWinningBid(getWinningDTO(item.getWinningBid(), dto));
        return dto;
    }
    
    protected List<Bid> makeDTO(List<Bid> bids, AuctionItem item) {
        List<Bid> dtos = new ArrayList<Bid>();
        for(Bid bid : bids) {
            Bid dto = new Bid(bid.getId());
            dto.setAmount(bid.getAmount());
            dto.setBidder(makeDTO(bid.getBidder(), dto));
            item.getBids().add(dto);
            dto.setItem(item);
            dtos.add(dto);
        }
        return dtos;
    }
    
    protected Bid getWinningDTO(Bid winningBid, AuctionItem item) {
        Bid dto = null;
        if (winningBid != null) {
            for(Bid bid : item.getBids()) {
                if (bid.getId() == winningBid.getId()) {
                    dto = bid;
                    break;
                }
            }
        }
        return dto;
    }
    
    protected Person makeDTO(Person person, Bid bid) {
        Person dto = new Person(person.getId());
        dto.setName(person.getName());
        dto.setUserId(person.getUserId());
        dto.getBids().add(bid);
        bid.setBidder(dto);
        return dto;
    }    
        
    @Timeout
    public void timeout(Timer timer) {
        try {
            long itemId = ((Long)timer.getInfo()).longValue();
            endAuction(itemId);
        }
        catch (Exception ex) {
            log.error("error ending auction for:" + timer.getInfo(), ex);
        }
    }
    
    public void endAuction(long itemId) throws MarketException {
        Connection connection = null;
        Session session = null;
        try {
            AuctionItem item = auctionItemDAO.getItem(itemId);
            if (item != null) {
                item.closeBids();          
                log.info("ending auction for:" + item);
    
                connection = connFactory.createConnection();
                session = connection.createSession(
                        false, Session.AUTO_ACKNOWLEDGE);
                publishSold(session, item);
            }
        }
        catch (JMSException jex) {
            log.error("error publishing jms message:", jex);
        }
        finally {
            try {
                if (session != null)    { session.close(); }
                if (connection != null) { connection.close(); }
            }
            catch (Exception ignored) {}
        }
    }
    
    protected void publishSold(Session session, AuctionItem item)
    throws JMSException {
    MessageProducer producer = null;
    try {
        producer = session.createProducer(sellTopic);
        MapMessage message = session.createMapMessage();
        message.setJMSType("sold");
        message.setLong("id", item.getId());
        message.setString("name", item.getName());
        message.setString("seller", item.getOwner().getUserId());
        message.setLong("startDate", item.getStartDate().getTime());
        message.setLong("endDate", item.getEndDate().getTime());
        message.setDouble("minBid", item.getMinBid());
        message.setDouble("bids", item.getBids().size());
        message.setString("buyerId", 
                          (item.getWinningBid() == null ?
                           "" : 
                           item.getWinningBid().getBidder().getUserId()));
        message.setDouble("winningBid", 
                (item.getHighestBid() == null ? 0.00 :
                    item.getHighestBid().getAmount()));            
        producer.send(message);
        log.debug("sent=" + message);
    }
    finally {
        if (producer != null)   { producer.close(); }
    }
}

}
