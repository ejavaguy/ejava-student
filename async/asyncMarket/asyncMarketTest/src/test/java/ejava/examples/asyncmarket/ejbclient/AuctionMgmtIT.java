package ejava.examples.asyncmarket.ejbclient;

import java.lang.reflect.UndeclaredThrowableException;

import java.util.Calendar;
import java.util.GregorianCalendar;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import ejava.examples.asyncmarket.bo.AuctionItem;
import ejava.examples.asyncmarket.bo.Bid;

public class AuctionMgmtIT extends MarketITBase {
    Logger log = LoggerFactory.getLogger(AuctionMgmtIT.class);
    
    @Test
    public void testAuction() throws Exception {
        log.info("*** testAuction ***");
        
        String userId = "mjones";
        String name = "Mary Jones";
        @SuppressWarnings("unused")
        long sellerId = usermgmt.createUser(userId, name);
        log.info("created user:" + name);
        
        AuctionItem item = new AuctionItem();
        item.setName("my stuff");
        item.setMinBid(4.00);
        Calendar cal = new GregorianCalendar();
        item.setStartDate(cal.getTime());
        cal.add(Calendar.SECOND, 10);
        item.setEndDate(cal.getTime());
        long itemId = seller.sellProduct(userId, item);
        log.info("created product:" + item);
        
        for(int i=0; i<3; i++) {
           usermgmt.createUser("asmith" + i, "Anna Smith" + i);            
        }
        buyer.bidProduct(itemId, "asmith0", 5.00);
        buyer.bidProduct(itemId, "asmith1", 6.00);
        buyer.bidProduct(itemId, "asmith2", 7.00);
        
        try {
            item = seller.getItem(itemId);            
            log.info("item:" + item);
            assertNotNull("item not found:" + itemId, item);
            assertEquals("unexpected number of bids:", 
                3, item.getBids().size());
            
            int items = auctionmgmt.checkAuction();
            log.debug("" + items + " still open for auction");
            assertEquals("unexpected number of open items", 1, items);

            assertFalse(item.isClosed());
            auctionmgmt.closeBidding(item.getId());
            item = seller.getItem(itemId);            
            assertTrue(item.isClosed());
            
            Bid winner = auctionmgmt.getWinningBid(item.getId());
            log.debug("winning bid=" + winner);
            assertEquals("asmith2", winner.getBidder().getUserId());

            items = auctionmgmt.checkAuction();
            log.debug("" + items + " still open for auction");
            assertEquals("unexpected number of open items", 0, items);            
        } catch (UndeclaredThrowableException ue) {
            fail("" + ue.getUndeclaredThrowable());
        }
        
    }
}
