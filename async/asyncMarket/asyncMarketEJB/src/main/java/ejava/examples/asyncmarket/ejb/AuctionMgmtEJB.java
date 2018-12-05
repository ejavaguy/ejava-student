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
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Schedule;
import javax.ejb.ScheduleExpression;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
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
import ejava.examples.asyncmarket.dao.AuctionItemDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class AuctionMgmtEJB implements AuctionMgmtRemote, AuctionMgmtLocal {
    private static final Logger logger = LoggerFactory.getLogger(AuctionMgmtEJB.class);
    
    @EJB
    private AuctionMgmtActionEJB actions;
    @Inject
    private AuctionItemDAO auctionItemDAO;
    @Inject
    private DtoMapper dtoMapper;
    
    @Resource
    private TimerService timerService;
    //injected
    long checkItemInterval;
    
    @Inject @JMSConnectionFactory("java:/JmsXA")
    private JMSContext jmsContext;
    @Resource(lookup="java:/jms/topic/ejava/examples/asyncMarket/topic1", type=Topic.class)
    private Destination sellTopic;
    
    @PostConstruct
    public void init() {
        logger.info("**** AuctionMgmtEJB init() ***");
        logger.debug("timerService={}", timerService);
        logger.debug("checkAuctionInterval={}", checkItemInterval);
        logger.debug("jmsContext={}", jmsContext);
        logger.debug("sellTopic={}", sellTopic);
    }
    
    public void cancelTimers() {
        logger.debug("canceling timers");
        for (Timer timer : (Collection<Timer>)timerService.getTimers()) {
            timer.cancel();
        }
    }
    public void initTimers(long delay) {
        cancelTimers();
        logger.debug("initializing timers, checkItemInterval={}", delay);
        timerService.createIntervalTimer(0L, delay, new TimerConfig("checkAuctionTimer", false));
    }
    public void initTimers(ScheduleExpression schedule) {
    	    cancelTimers();
        logger.debug("initializing timers, schedule={}", schedule);
    	    timerService.createCalendarTimer(schedule, new TimerConfig("checkAuctionTimer", false));
    }
    
    public void closeBidding(long itemId) throws ResourceNotFoundException {
        AuctionItem item = auctionItemDAO.getItem(itemId);
        if (item == null) {
            throw new ResourceNotFoundException("itemId[%s] not found", itemId);
        }

        try {
            item.closeBids();
            logger.debug("closed bidding for item: {}", item);
        }
        catch (Exception ex) {
            logger.error("error closing bid", ex);
            throw new InternalErrorException("error closing bid:" + ex);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Bid getWinningBid(long itemId) throws ResourceNotFoundException {
        AuctionItem item=auctionItemDAO.getItem(itemId);
        if (item==null) {
            throw new ResourceNotFoundException("bid[%s] not found", itemId);
        }
        
        try {
            return dtoMapper.toDTO(item.getWinningBid());
        } catch (Exception ex) {
            throw new InternalErrorException("error returning winning bid:%s", ex.toString());
        }
    }

    
    @Timeout
    @Schedule(second="*/10", minute ="*", hour="*", dayOfMonth="*", month="*", year="*", persistent=false)
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void execute(Timer timer) {
        logger.info("timer fired: {}", timer);
        try {
            checkAuction();
        }
        catch (Exception ex) {
            logger.error("error checking auction", ex);
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int checkAuction() {
        logger.info("checking auctions");
        int index = 0;            
        try {
            for (List<AuctionItem> items = auctionItemDAO.getAvailableItems(index, 10); items.size()>0; ) {
                for(AuctionItem item : items) {
                    publishAvailableItem(item);
                }
                index += items.size();
                items = auctionItemDAO.getAvailableItems(index, 10);
            }
            logger.debug("processed {} active items", index);
            return index;
        }
        catch (JMSException ex) {
            logger.error("error publishing auction item updates", ex);
            return index;
        }
    }

    protected void publishAvailableItem(AuctionItem item) throws JMSException {
        JMSProducer producer = jmsContext.createProducer();
        MapMessage message = jmsContext.createMapMessage();
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
        producer.send(sellTopic, message);
        logger.debug("sent={}", message);
    }

    
    public void removeBid(long bidId) throws ResourceNotFoundException {
        Bid bid = auctionItemDAO.getBid(bidId);
        if (bid==null) {
            throw new ResourceNotFoundException("bidId[%s] not found", bidId);
        }
        
        try {
            auctionItemDAO.removeBid(bid);        }
        catch (Exception ex) {
            logger.error("error removing bid", ex);
            throw new InternalErrorException("error removing bid[%d]:%s", bidId, ex.toString());
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<AuctionItem> getItems(int index, int count) {
        try {
            return dtoMapper.toDTO(auctionItemDAO.getItems(index, count));
        }
        catch (Exception ex) {
            logger.error("error getting auction items", ex);
            throw new InternalErrorException("error getting auction items: %s", ex.toString());
        }
    }

    public void removeItem(long itemId) {
        try {
            auctionItemDAO.removeItem(itemId);
        }
        catch (Exception ex) {
            logger.error("error removing auction items", ex);
            throw new InternalErrorException("error removing auction item[%d]: %s", itemId, ex.toString());
        }
    }

    /**
     * Perform action synchronously while this caller waits
     */
	@Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void workSync(int count, long delay) {
        DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
        
        long startTime = System.currentTimeMillis();
        for (int i=0; i<count; i++) {
            logger.info("{} issuing sync request, delay={}", df.format(new Date()), delay);
            	@SuppressWarnings("unused")
            	Date date = actions.doWorkSync(delay);
            	logger.info("sync waitTime={} msecs", System.currentTimeMillis()-startTime);
        }
        	long syncTime = System.currentTimeMillis() - startTime;
        	logger.info("workSync time={} msecs", syncTime);
	}    

	/**
	 * Perform action async from this caller
	 */
	@Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void workAsync(int count, long delay) {
        DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
        
        List<Future<Date>> results = new ArrayList<Future<Date>>();
            //issue requests
        long startTime = System.currentTimeMillis();
        for (int i=0; i<count; i++) {
            	logger.info("{} issuing async request, delay={}", df.format(new Date()), delay);
            	Future<Date> date = actions.doWorkAsync(delay);
            	results.add(date);
            	logger.info("async waitTime={} msecs", System.currentTimeMillis()-startTime);
        }
        
            //process results
        for (Future<Date> f: results) {
            	logger.info("{} getting async response", df.format(new Date()));
            	try {
    				@SuppressWarnings("unused")
    				Date date = f.get();
    			} catch (ExecutionException | InterruptedException ex) {
    				logger.error("unexpected error on future.get()", ex);
    				throw new EJBException("unexpected error during future.get():"+ex);
    			}
            	logger.info("{} got async response", df.format(new Date()));
        }
        	long asyncTime = System.currentTimeMillis() - startTime;
        	logger.info("workAsync time={} msecs", asyncTime);
	}    
}
