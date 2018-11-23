package ejava.examples.asyncmarket.ejb;

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
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.MapMessage;
import javax.jms.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.asyncmarket.bo.AuctionItem;
import ejava.examples.asyncmarket.bo.Bid;
import ejava.examples.asyncmarket.bo.Person;
import ejava.examples.asyncmarket.dao.AuctionItemDAO;
import ejava.examples.asyncmarket.dao.PersonDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class SellerEJB
    implements SellerLocal, SellerRemote {
    private static final Logger logger = LoggerFactory.getLogger(SellerEJB.class);
    
    @Inject @JMSConnectionFactory("java:/JmsXA")
    private JMSContext jmsContext;
    @Resource(lookup="java:/jms/topic/ejava/examples/asyncMarket/topic1", type=Topic.class)
    private Destination sellTopic;
    
    @Resource
    private TimerService timerService;
    @Resource
    private SessionContext ctx;
//    @PersistenceContext(unitName="asyncMarket")
//    private EntityManager em;

    @Inject
    private PersonDAO sellerDAO;
    @Inject
    private AuctionItemDAO auctionItemDAO;
    @Inject
    private DtoMapper dtoMapper;
    
    @PostConstruct
    public void init() {
        logger.info("******************* SellerEJB Created ******************");
        logger.debug("ctx={}", ctx);
        logger.debug("connFactory={}", jmsContext);
        logger.debug("sellTopic={}", sellTopic);
        logger.debug("timerService={}", timerService);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public long sellProduct(String sellerId, AuctionItem item) throws ResourceNotFoundException {
        logger.debug("sellProduct(sellerId={},item={})", sellerId, item);
        
        Person seller = sellerDAO.getPersonByUserId(sellerId);
        if (seller==null) {
            throw new ResourceNotFoundException("seller[%s] not found", sellerId);
        }
        
        try {
            seller.getItems().add(item);
            item.setOwner(seller);
            auctionItemDAO.createItem(item);
            
            publishForSale(item);
            timerService.createTimer(item.getEndDate(), new Long(item.getId()));
            return item.getId();
        }
        catch (JMSException ex) {
            logger.error("error publishing sale", ex);
            ctx.setRollbackOnly();
            throw new EJBException("error publishing sell");
        }
        catch (Exception ex) {
            logger.error("error selling product", ex);
            ctx.setRollbackOnly();
            throw new InternalErrorException("error selling product: %s", ex);
        }
    }    
    
    protected void publishForSale(AuctionItem item) throws JMSException {
        JMSProducer producer = jmsContext.createProducer();
        MapMessage message = jmsContext.createMapMessage();
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
        producer.send(sellTopic, message);
        logger.debug("sent=" + message);
    }


    public AuctionItem getItem(long itemId) throws ResourceNotFoundException {
        AuctionItem item = auctionItemDAO.getItem(itemId);
        if (item==null) {
            throw new ResourceNotFoundException("itemId[%d] not found", itemId);            
        }
        
        try {
            AuctionItem dto = dtoMapper.toDTO(item);
            logger.debug("dao item=" + item);
            logger.debug("dto item=" + dto);
            return dto;
        }
        catch (Exception ex) {
            logger.error("error getting auction item", ex);
            throw new InternalErrorException("error getting auction item:%s", ex);
        }
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
    
    @Timeout
    public void timeout(Timer timer) {
        try {
            long itemId = ((Long)timer.getInfo()).longValue();
            endAuction(itemId);
        }
        catch (ResourceNotFoundException ex) {
            //ignored
        }
        catch (Exception ex) {
            logger.error("error ending auction for:" + timer.getInfo(), ex);
        }
    }
    
    public void endAuction(long itemId) throws ResourceNotFoundException  {
        AuctionItem item = auctionItemDAO.getItem(itemId);
        if (item==null) {
            throw new ResourceNotFoundException("itemId[%d] not found", itemId);
        }
        
        try {
            item.closeBids();          
            logger.info("ending auction for:" + item);    
            publishSold(item);
        } catch (JMSException ex) {
            logger.error("error publishing jms message", ex);
            throw new InternalErrorException("error publishing jms message", ex);
        } catch (Exception ex) {
            logger.error("error ending auction:" + itemId, ex);
            throw new InternalErrorException("error ending auction[%d]: %s", itemId, ex);
        }
    }
    
    protected void publishSold(AuctionItem item) throws JMSException {
        JMSProducer producer = jmsContext.createProducer();
        MapMessage message = jmsContext.createMapMessage();
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
        producer.send(sellTopic, message);
        logger.debug("sent=" + message);
    }
}
