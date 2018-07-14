package ejava.examples.asyncmarket.ejb;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.AsyncResult;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Schedule;
import javax.ejb.ScheduleExpression;
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
public class AuctionMgmtEJB implements AuctionMgmtRemote, AuctionMgmtLocal {
    private static final Log log = LogFactory.getLog(AuctionMgmtEJB.class);
    
    @PersistenceContext(unitName="asyncMarket")
    private EntityManager em;
    
    private @EJB AuctionMgmtActionEJB actions;
    
    private AuctionItemDAO auctionItemDAO;
    private PersonDAO userDAO;
    
    @Resource
    private TimerService timerService;
    //injected
    long checkItemInterval;
    
    @Resource(lookup="java:/JmsXA")
    //@Resource(name="jms/ConnectionFactory")
    private ConnectionFactory connFactory;
    @Resource(lookup="java:/topic/ejava/examples/asyncMarket/topic1", type=Topic.class)
    private Destination sellTopic;
    
    @PostConstruct
    public void init() {
        log.info("**** AuctionMgmtEJB init() ***");
        log.debug("timerService=" + timerService);
        log.debug("checkAuctionInterval=" + checkItemInterval);
        log.debug("connFactory=" + connFactory);
        log.debug("sellTopic=" + sellTopic);
        
        auctionItemDAO = new JPAAuctionItemDAO();
        ((JPAAuctionItemDAO)auctionItemDAO).setEntityManager(em);
        
        userDAO = new JPAPersonDAO();
        ((JPAPersonDAO)userDAO).setEntityManager(em);
    }
    
    public void cancelTimers() {
        log.debug("canceling timers");
        for (Timer timer : (Collection<Timer>)timerService.getTimers()) {
            timer.cancel();
        }
    }
    public void initTimers(long delay) {
        cancelTimers();
        log.debug("initializing timers, checkItemInterval="+delay);
        timerService.createTimer(0,delay, "checkAuctionTimer");
    }
    public void initTimers(ScheduleExpression schedule) {
    	cancelTimers();
        log.debug("initializing timers, schedule="+schedule);
    	timerService.createCalendarTimer(schedule);
    }
    
    public void closeBidding(long itemId) throws MarketException {
        AuctionItem item = auctionItemDAO.getItem(itemId);
        if (item == null) {
            throw new MarketException("itemId not found:" + itemId);
        }

        try {
            item.closeBids();
            log.debug("closed bidding for item:" + item);
        }
        catch (Exception ex) {
            log.error("error closing bid", ex);
            throw new MarketException("error closing bid:" + ex);
        }
    }

    public Bid getWinningBid(long itemId) throws MarketException {
        AuctionItem item=null;
        try {
            item = auctionItemDAO.getItem(itemId);
            if (item != null) {
               return makeDTO(item.getWinningBid());
            }
        }
        catch (Exception ex) {
            log.error("error closing bid", ex);
            throw new MarketException("error closing bid:" + ex);
        }
        throw new MarketException("itemId not found:" + itemId);
    }

    private Bid makeDTO(Bid bid) {
        Bid dto = new Bid(bid.getId());
        dto.setAmount(bid.getAmount());
        dto.setBidder(makeDTO(bid.getBidder(), dto));
        dto.setItem(makeDTO(bid.getItem(), dto));
        return dto;
    }
    
    private Person makeDTO(Person person, Bid bid) {
        Person dto = new Person(person.getId());
        dto.setUserId(person.getUserId());
        dto.getBids().add(bid);
        bid.setBidder(dto);        
        dto.setVersion(person.getVersion());
        return dto;
    }
    
    private AuctionItem makeDTO(AuctionItem item, Bid bid) {
        AuctionItem dto = new AuctionItem(item.getId());
        dto.setName(item.getName());
        dto.setVersion(item.getVersion());
        dto.setStartDate(item.getStartDate());
        dto.setEndDate(item.getEndDate());
        dto.setMinBid(item.getMinBid());
        dto.setWinningBid(bid);
        dto.setClosed(item.isClosed());
        return dto;
    }

    
    @Timeout
    @Schedule(second="*/10", minute ="*", hour="*", dayOfMonth="*", month="*", year="*", persistent=false)
    public void execute(Timer timer) {
        log.info("timer fired:" + timer);
        try {
            checkAuction();
        }
        catch (Exception ex) {
            log.error("error checking auction", ex);
        }
    }
    
    public int checkAuction() throws MarketException {
        log.info("checking auctions");
        Connection connection = null;
        Session session = null;
        int index = 0;            
        try {
            connection = connFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            List<AuctionItem> items = null;
            do { 
                items = auctionItemDAO.getAvailableItems(index, 10);
                for(AuctionItem item : items) {
                    publishAvailableItem(session, item);
                }
                index += items.size();
            } while (items.size() > 0);
            log.debug("processed " + index + " active items");
            return index;
        }
        catch (JMSException ex) {
            log.error("error publishing auction item updates", ex);
            return index;
        }
        finally {
            try {
                if (session != null) { session.close(); }
                if (connection != null) { connection.close(); }
            } catch (JMSException ignored) {}
        }
    }

    protected void publishAvailableItem(Session session, AuctionItem item)
        throws JMSException {
        MessageProducer producer = null;
        try {
            producer = session.createProducer(sellTopic);
            MapMessage message = session.createMapMessage();
            message.setJMSType("saleUpdate");
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

    
    public void removeBid(long bidId) throws MarketException {
        try {
            Bid bid = auctionItemDAO.getBid(bidId);
            auctionItemDAO.removeBid(bid);        }
        catch (Exception ex) {
            log.error("error removing bid", ex);
            throw new MarketException("error removing bid:" + ex);
        }
    }
    
    public List<AuctionItem> getItems(int index, int count) 
        throws MarketException {
        try {
            return makeDTO(auctionItemDAO.getItems(index, count));
        }
        catch (Exception ex) {
            log.error("error getting auction items", ex);
            throw new MarketException("error getting auction items" + ex);
        }
    }

    public void removeItem(long id) throws MarketException {
        try {
            auctionItemDAO.removeItem(id);
        }
        catch (Exception ex) {
            log.error("error removing auction items", ex);
            throw new MarketException("error removing auction items" + ex);
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

    /**
     * Perform action synchronously while caller waits.
     */
	@Override
	public void workSync(int count, long delay) {
        DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
        
        long startTime = System.currentTimeMillis();
        for (int i=0; i<count; i++) {
        	log.info(String.format("%s issuing sync request, delay=%d", df.format(new Date()), delay));
        	@SuppressWarnings("unused")
			Date date= actions.doWorkSync(delay);
        	log.info(String.format("sync waitTime=%d msecs", System.currentTimeMillis()-startTime));
        }
    	long syncTime = System.currentTimeMillis() - startTime;
    	log.info(String.format("workSync time=%d msecs", syncTime));
	}    

	/**
	 * Perform action async from caller.
	 */
	@Override
	public void workAsync(int count, long delay) {
        DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
        
        long startTime = System.currentTimeMillis();
        List<Future<Date>> results = new ArrayList<Future<Date>>();
        for (int i=0; i<count; i++) {
        	log.info(String.format("%s issuing async request, delay=%d", df.format(new Date()), delay));
        	Future<Date> date = actions.doWorkAsync(delay);
        	results.add(date);
        	log.info(String.format("async waitTime=%d msecs", System.currentTimeMillis()-startTime));
        }
        for (Future<Date> f: results) {
        	log.info(String.format("%s getting async response", df.format(new Date())));
        	try {
				@SuppressWarnings("unused")
				Date date = f.get();
			} catch (Exception ex) {
				log.error("unexpected error on future.get()", ex);
				throw new EJBException("unexpected error during future.get():"+ex);
			}
        	log.info(String.format("%s got async response", df.format(new Date())));
        }
    	long asyncTime = System.currentTimeMillis() - startTime;
    	log.info(String.format("workAsync time=%d msecs", asyncTime));
	}    
}
