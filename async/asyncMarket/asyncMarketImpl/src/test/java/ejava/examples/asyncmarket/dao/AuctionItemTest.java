package ejava.examples.asyncmarket.dao;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import ejava.examples.asyncmarket.MarketTestBase;
import ejava.examples.asyncmarket.bo.AuctionItem;
import ejava.examples.asyncmarket.bo.Bid;
import ejava.examples.asyncmarket.bo.Person;

public class AuctionItemTest extends MarketTestBase {
    Log log = LogFactory.getLog(AuctionItemTest.class);
    
    @Test
    public void testAuction() {
        log.info("*** testAuction ***");
        
        Person seller = new Person();
        seller.setName("joe smith");
        seller.setUserId("jsmith");        
        personDAO.createPerson(seller);
        
        assertEquals("unexpected number of available items", 
                0, auctionItemDAO.getAvailableItems(0, 100).size());
        
        AuctionItem item = new AuctionItem();
        item.setName("best of steely dan CD");
        Calendar cal = new GregorianCalendar();
        item.setStartDate(cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, 5);
        item.setEndDate(cal.getTime());
        item.setMinBid(5.00);
        item.setOwner(seller);
        seller.getItems().add(item);
        auctionItemDAO.createItem(item);

        assertEquals("unexpected number of available items", 
                1, auctionItemDAO.getAvailableItems(0, 100).size());        

        Collection<Person> bidders = new ArrayList<Person>();
        for (int i=0; i<5; i++) {
            Person person = new Person();
            person.setName("mary jones");
            person.setUserId("mjones" + i);
            personDAO.createPerson(person);
            bidders.add(person);
        }
        em.flush();
        
        for (Person p : bidders) {
            Bid highest = item.getHighestBid();
            Bid bid = new Bid();
            bid.setBidder(p);
            bid.setItem(item);            
            bid.setAmount(highest == null ? 5.00 : highest.getAmount() + 1);
            auctionItemDAO.addBid(item.getId(), bid);
            em.refresh(p);
        }        
        
        item.closeBids();
        em.flush();
        
        assertEquals("unexpected number of bids for item:" + 
                item.getBids().size(),
                5, item.getBids().size());
        assertEquals("unexpected number of items for seller:" + 
                seller.getItems().size(),
                1, seller.getItems().size());
        for (Person p : bidders) {
            assertEquals("unexpected number of bids for bidder:" + 
                    p.getBids().size(),
                    1, p.getBids().size());
        }        
        assertEquals("unexpected number of available items", 
                0, auctionItemDAO.getAvailableItems(0, 100).size());
        
    }    
}
