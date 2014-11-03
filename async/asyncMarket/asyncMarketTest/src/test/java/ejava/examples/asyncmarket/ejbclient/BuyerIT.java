package ejava.examples.asyncmarket.ejbclient;

import java.lang.reflect.UndeclaredThrowableException;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import ejava.examples.asyncmarket.bo.AuctionItem;
import ejava.examples.asyncmarket.bo.Order;

public class BuyerIT extends MarketITBase {
    Log log = LogFactory.getLog(BuyerIT.class);
    InitialContext jndi;
    
    @Test
    public void testOrder() throws Exception {
        log.info("*** testOrder ***");
        
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
            
            assertEquals("unexpected number of available items", 1, 
                    buyer.getAvailableItems(0, 100).size());
            long order1Id = buyer.placeOrder(item.getId(), userId, 10);
            Order order1 = buyer.getOrder(order1Id);
            assertNotNull(order1);
            assertEquals("unexpected orderId", 
                    order1.getItem().getId(), item.getId());
            
        } catch (UndeclaredThrowableException ue) {
            log.error("undeclared exception:", ue.getCause());
            fail("" + ue.getUndeclaredThrowable());
        }        
    }
}
