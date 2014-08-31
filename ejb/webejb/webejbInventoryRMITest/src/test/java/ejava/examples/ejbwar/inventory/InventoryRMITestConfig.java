package ejava.examples.ejbwar.inventory;

import java.io.IOException;

import java.io.InputStream;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import ejava.examples.ejbwar.customer.ejb.CustomerMgmtRemote;
import ejava.examples.ejbwar.inventory.rmi.InventoryMgmtRemote;
import ejava.util.ejb.EJBClient;

/**
 * This class provides configuration and runtime objects used for testing
 * the inventory RMI interface.
 */
public class InventoryRMITestConfig {
	private String inventoryJNDIName;
	private String customerJNDIName;
	private Context jndi;
	private InventoryMgmtRemote inventoryClient;	
	private CustomerMgmtRemote customerClient;
	private Properties props = new Properties();
	
	/**
	 * Load in overrides for hard-coded defaults.
	 * @param resource
	 * @throws IOException
	 */
	public InventoryRMITestConfig(String resource) throws IOException {
		InputStream is = getClass().getResourceAsStream(resource);
		if (is!=null) {
			try {
				props.load(is);
			} finally {
				is.close();
			}
		}
	}
	
	/**
	 * Return an initial context to use for lookups.
	 * @return
	 * @throws NamingException
	 */
	public Context jndi() throws NamingException {
		if (jndi==null) {
			jndi=new InitialContext();
		}
		return jndi;
	}
	
	public void close() throws NamingException {
		if (jndi!=null) {
			jndi.close();
			jndi=null;
		}
	}
	
	/**
	 * Return the JNDI name for the inventory management EJB RMI facade.
	 * @return
	 */
	public String inventoryJNDIName() {
		if (inventoryJNDIName==null) {
			String warName = props.getProperty("war.name", "jaxrsInventoryWAR");
			String ejbName = props.getProperty("ejb.name", "InventoryMgmtRMIEJB");
			String remoteName = props.getProperty("remote.name", InventoryMgmtRemote.class.getName());
			inventoryJNDIName = EJBClient.getRemoteLookupName(warName, ejbName, remoteName);
		}
		return inventoryJNDIName;
	}
	
	/**
	 * Return the JNDI name for the inventory management EJB RMI facade.
	 * @return
	 */
	public String customerJNDIName() {
		if (customerJNDIName==null) {
			String warName = props.getProperty("war.name", "jaxrsInventoryWAR");
			String ejbName = props.getProperty("ejb.name", "CustomerMgmtEJB");
			String remoteName = props.getProperty("remote.name", CustomerMgmtRemote.class.getName());
			customerJNDIName = EJBClient.getRemoteLookupName(warName, ejbName, remoteName);
		}
		return customerJNDIName;
	}
	
	
	
	/**
	 * Return the RMI stub that can be used to communicate directly with the 
	 * inventory management application.
	 * @return
	 * @throws NamingException
	 */
	public InventoryMgmtRemote inventoryClient() throws NamingException {
		if (inventoryClient==null) {
			inventoryClient = (InventoryMgmtRemote)jndi().lookup(inventoryJNDIName());
		}
		return inventoryClient;
	}
	
	/**
	 * Returns an RMI stub used to communicate withthe integrated
	 * external EJB module.
	 * @return
	 */
	public CustomerMgmtRemote customerClient() throws NamingException {
		if (customerClient == null) {
			customerClient = (CustomerMgmtRemote)jndi().lookup(customerJNDIName());
		}
		return customerClient;
	}
	
}
