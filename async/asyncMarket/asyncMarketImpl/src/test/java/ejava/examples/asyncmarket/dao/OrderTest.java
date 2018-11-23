package ejava.examples.asyncmarket.dao;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import ejava.examples.asyncmarket.MarketTestBase;
import ejava.examples.asyncmarket.bo.AuctionItem;
import ejava.examples.asyncmarket.bo.Bid;
import ejava.examples.asyncmarket.bo.Order;
import ejava.examples.asyncmarket.bo.Person;

public class OrderTest extends MarketTestBase {
    Logger logger = LoggerFactory.getLogger(OrderTest.class);

    @Test
    public void testOrder() throws Exception {
        logger.info("*** testOrder ***");

        Person seller = new Person();
        seller.setName("joe smith");
        seller.setUserId("jsmith");
        personDAO.createPerson(seller);
        em.flush();

        AuctionItem item = new AuctionItem();
        item.setName("best of steely dan CD");
        Calendar cal = new GregorianCalendar();
        item.setStartDate(cal.getTime());
        cal.add(Calendar.SECOND, 2);
        item.setEndDate(cal.getTime());
        item.setMinBid(5.00);
        item.setOwner(seller);
        seller.getItems().add(item);
        auctionItemDAO.createItem(item);
        em.flush();

        Person buyer1 = new Person();
        buyer1.setUserId("asmith");
        buyer1.setName("Alan Smith");
        personDAO.createPerson(buyer1);
        em.flush();

        Order order1 = new Order();
        order1.setBuyer(buyer1);
        order1.setItem(item);
        order1.setMaxBid(20);
        orderDAO.createOrder(order1);
        em.flush();

        Person buyer2 = new Person();
        buyer2.setUserId("jjones");
        buyer2.setName("Joe Jones");
        personDAO.createPerson(buyer2);
        em.flush();

        Order order2 = new Order();
        order2.setBuyer(buyer2);
        order2.setItem(item);
        order2.setMaxBid(8);
        orderDAO.createOrder(order2);
        em.flush();

        while (!item.isClosed()) {
            for (Order o : orderDAO.getOrdersforItem(item.getId(), 0, 100)) {
                Bid bid = new Bid();
                String bidType=null;
                if (item.getHighestBid() == null) {
                    bid.setAmount(item.getMinBid());
                    bidType = "initial";
                } else if ((item.getHighestBid().getAmount() < o.getMaxBid())
                        && item.getHighestBid().getBidder().getId() != o.getBuyer().getId()) {
                    bid.setAmount(item.getHighestBid().getAmount() + 1.00);
                    bidType = "new";
                }
                if (bid.getAmount() > 0) {
                    bid.setBidder(o.getBuyer());
                    bid.setItem(item);
                    item.addBid(bid);
                    auctionItemDAO.createItem(item); //<== fire cascade to persist BID
                    logger.debug("added new bid {}", bidType, bid);
                }
            }
            if (item.getEndDate().before(new Date())) {
                logger.debug("closing out bidding");
                item.closeBids();
            }
            else { 
                Thread.sleep(500);
            }
        }
        
        assertEquals("unexpected winning buyer",
                buyer1.getId(), item.getWinningBid().getBidder().getId());
    }
}
