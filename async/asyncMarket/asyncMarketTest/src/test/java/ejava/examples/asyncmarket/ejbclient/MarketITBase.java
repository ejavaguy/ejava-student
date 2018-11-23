package ejava.examples.asyncmarket.ejbclient;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;

import javax.naming.InitialContext;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.asyncmarket.Buyer;
import ejava.examples.asyncmarket.Seller;
import ejava.examples.asyncmarket.UserMgmt;
import ejava.examples.asyncmarket.bo.AuctionItem;
import ejava.examples.asyncmarket.bo.Person;
import ejava.examples.asyncmarket.ejb.AuctionMgmtRemote;
import ejava.examples.asyncmarket.ejb.BuyerRemote;
import ejava.examples.asyncmarket.ejb.SellerRemote;
import ejava.examples.asyncmarket.ejb.UserMgmtRemote;
import ejava.util.ejb.EJBClient;
import junit.framework.TestCase;

public abstract class MarketITBase extends TestCase {
	private static final Logger logger = LoggerFactory.getLogger(MarketITBase.class);
	protected static String auctionmgmtJNDI = System.getProperty("jndi.name.auctionmgmt",
		EJBClient.getRemoteLookupName("asyncMarketEAR", "asyncMarketEJB", 
				"AuctionMgmtEJB", AuctionMgmtRemote.class.getName()));
	protected static String usermgmtJNDI = System.getProperty("jndi.name.usermgmt",
		EJBClient.getRemoteLookupName("asyncMarketEAR", "asyncMarketEJB", 
			"UserMgmtEJB", UserMgmtRemote.class.getName()));
	protected static String sellerJNDI = System.getProperty("jndi.name.seller",
		EJBClient.getRemoteLookupName("asyncMarketEAR", "asyncMarketEJB", 
			"SellerEJB", SellerRemote.class.getName()));
	protected static String buyerJNDI = System.getProperty("jndi.name.buyer",
		EJBClient.getRemoteLookupName("asyncMarketEAR", "asyncMarketEJB", 
			"BuyerEJB", BuyerRemote.class.getName()));
	protected static InitialContext jndi;
	protected AuctionMgmtRemote auctionmgmt;
	protected UserMgmt usermgmt;
	protected Seller seller;
	protected Buyer buyer;
	
	@BeforeClass
	public static void setUpClass() throws Exception {
    	logger.info("*** setUpClass() ***");
		//give application time to fully deploy
		if (Boolean.parseBoolean(System.getProperty("cargo.startstop", "false"))) {
			long waitTime=15000;
	    	    logger.info("pausing {} secs for server deployment to complete", waitTime/1000);
	    	    Thread.sleep(waitTime);
		}
		else {
	    	    logger.info(String.format("startstop not set"));
		}
	}

	@Before
    public void setUp() throws Exception {
        logger.debug("getting jndi initial context");
        jndi = new InitialContext();    
        logger.debug("jndi=" + jndi.getEnvironment());
        
        logger.debug("looking up:" + auctionmgmtJNDI);
        auctionmgmt = (AuctionMgmtRemote)jndi.lookup(auctionmgmtJNDI);

        logger.debug("looking up:" + usermgmtJNDI);
        usermgmt = (UserMgmtRemote)jndi.lookup(usermgmtJNDI);

        logger.debug("looking up:" + sellerJNDI);
        seller = (SellerRemote)jndi.lookup(sellerJNDI);
        
        logger.debug("looking up:" + buyerJNDI);
        buyer = (BuyerRemote)jndi.lookup(buyerJNDI);
        
        try {
            cleanup();
        } catch (UndeclaredThrowableException ue) {
            logger.error("error in cleanup:", ue.getUndeclaredThrowable());
            fail("" + ue.getUndeclaredThrowable());
        }
    }
	
	@After
	public void tearDown() throws Exception {
        auctionmgmt.cancelTimers();
		if (jndi!=null) {
			jndi.close();
			jndi=null;
		}
	}
    
	/**
	 * Use remote interfaces to clear DB for next test.
	 * @throws Exception
	 */
    private void cleanup() throws Exception {
        auctionmgmt.cancelTimers();
        
        List<AuctionItem> items = null;
        int index=0;
        do {
            items = auctionmgmt.getItems(index, 10);
            for (AuctionItem item : items) {
                logger.debug("removing item:" + item);
                auctionmgmt.removeItem(item.getId());
            }
            
        } while (items.size() > 0);
        
        List<Person> users = null;
        index=0;
        do {
            users = usermgmt.getUsers(index, 10);
            for (Person user : users) {
                logger.debug("removing user:" + user.getUserId());
                usermgmt.removeUser(user.getUserId());
            }
            index += users.size();
        } while (users.size() > 0);       
    }
	
}
 