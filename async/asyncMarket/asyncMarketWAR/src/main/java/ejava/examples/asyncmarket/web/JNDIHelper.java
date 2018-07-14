package ejava.examples.asyncmarket.web;

import java.util.Enumeration;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.asyncmarket.AuctionMgmt;
import ejava.examples.asyncmarket.Buyer;
import ejava.examples.asyncmarket.Seller;
import ejava.examples.asyncmarket.UserMgmt;
import ejava.examples.asyncmarket.ejb.AuctionMgmtLocal;
import ejava.examples.asyncmarket.ejb.AuctionMgmtRemote;
import ejava.examples.asyncmarket.ejb.BuyerLocal;
import ejava.examples.asyncmarket.ejb.BuyerRemote;
import ejava.examples.asyncmarket.ejb.SellerLocal;
import ejava.examples.asyncmarket.ejb.SellerRemote;
import ejava.examples.asyncmarket.ejb.UserMgmtLocal;
import ejava.examples.asyncmarket.ejb.UserMgmtRemote;
import ejava.util.ejb.EJBClient;

/**
 * This is a helper class used to help locate EJBs when running in a 
 * remote web container -- such as Jetty.
 *  
 * TODO: factor out JNDI properties into a jndi.properties file
 */
public class JNDIHelper {
    private static final Logger log = LoggerFactory.getLogger(JNDIHelper.class);
    public static final String AUCTION_MGMT_REMOTE_JNDI=
    	EJBClient.getRemoteLookupName("asyncMarketEAR", "asyncMarketEJB", 
			"AuctionMgmtEJB", AuctionMgmtRemote.class.getName());    
	public static final String USER_MGMT_REMOTE_JNDI =
		EJBClient.getRemoteLookupName("asyncMarketEAR", "asyncMarketEJB", 
			"UserMgmtEJB", UserMgmtRemote.class.getName());
	public static final String SELLER_REMOTE_JNDI = 
		EJBClient.getRemoteLookupName("asyncMarketEAR", "asyncMarketEJB", 
			"SellerEJB", SellerRemote.class.getName());	
	public static final String BUYER_REMOTE_JNDI = 
		EJBClient.getRemoteLookupName("asyncMarketEAR", "asyncMarketEJB", 
			"BuyerEJB", BuyerRemote.class.getName());

    private Context jndi;
    
    public JNDIHelper(ServletContext context) throws NamingException {
    	jndi=getInitialContext(context);
    }
    public void close() {
    	try {
    		jndi.close();
    	} catch (NamingException ex) {
    		throw new RuntimeException("unexpected error during JNDI.close()", ex);
    	}
    }
    
    public AuctionMgmt getAuctionMgmt() throws NamingException {
    	return lookup(AuctionMgmt.class, jndi, AUCTION_MGMT_REMOTE_JNDI);
    }
    
    public UserMgmt getUserMgmt() throws NamingException {
    	return lookup(UserMgmt.class, jndi, USER_MGMT_REMOTE_JNDI);
    }

    public Seller getSeller() throws NamingException {
    	return lookup(Seller.class, jndi, SELLER_REMOTE_JNDI);
    }
    
    public Buyer getBuyer() throws NamingException {
    	return lookup(Buyer.class, jndi, BUYER_REMOTE_JNDI);
    }

    
    private Context getInitialContext(ServletContext context) 
        throws NamingException {
        
        //build an InitialContext from Servlet.init properties in web.xml
        Properties jndiProperties = new Properties();
        for(Enumeration<?> e=context.getInitParameterNames();
            e.hasMoreElements(); ) {
            String key = (String)e.nextElement();
            String value=(String)context.getInitParameter(key);
            if (key.startsWith("java.naming")) {
                jndiProperties.put(key, value);
            }                    
        }
        log.debug("jndiProperties=" + jndiProperties);
        InitialContext jndi = new InitialContext(jndiProperties);
        log.debug("jndi=" + jndiProperties);
        return jndi;
    }

    @SuppressWarnings("unchecked")
	private <T> T lookup(Class<T> lazz, Context jndi, String remoteJNDI) 
    		throws NamingException { 
        T object = null;            
        object = (T)jndi.lookup(remoteJNDI);
        log.debug("object=" + object);
        return object;
    }
}
