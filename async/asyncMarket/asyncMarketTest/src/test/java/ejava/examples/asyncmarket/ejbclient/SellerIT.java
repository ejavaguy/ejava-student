package ejava.examples.asyncmarket.ejbclient;

import java.lang.reflect.UndeclaredThrowableException;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import ejava.examples.asyncmarket.bo.AuctionItem;

public class SellerIT extends MarketITBase {
    Log log = LogFactory.getLog(SellerIT.class);
    
    @Test
    public void testSeller() throws Exception {
        log.info("*** testSeller ***");
        
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
        
        try {
            item = seller.getItem(itemId);            
            log.info("item:" + item);
            assertNotNull("item not found:" + itemId, item);
            assertFalse(item.isClosed());
            
        } catch (UndeclaredThrowableException ue) {
            fail("" + ue.getUndeclaredThrowable());
        }        
    }
}
