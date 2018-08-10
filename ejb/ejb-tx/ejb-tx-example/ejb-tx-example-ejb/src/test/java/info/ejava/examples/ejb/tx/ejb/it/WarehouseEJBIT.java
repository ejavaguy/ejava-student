package info.ejava.examples.ejb.tx.ejb.it;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import info.ejava.examples.ejb.tx.bo.Product;
import info.ejava.examples.ejb.tx.bo.Shipment;
import info.ejava.examples.ejb.tx.ejb.MyCheckedProductException;
import info.ejava.examples.ejb.tx.ejb.MyCheckedRollbackProductException;
import info.ejava.examples.ejb.tx.ejb.UnexpectedState;
import info.ejava.examples.ejb.tx.ejb.WarehouseRemote;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WarehouseEJBIT  {
	private static final Logger logger = LoggerFactory.getLogger(WarehouseEJBIT.class);
    private static final String warehouseJNDI = System.getProperty("warehouse.jndi.name",
            "ejb:/ejb-tx-example-ejb/WarehouseFacadeEJB!" + WarehouseRemote.class.getName());
	private Context jndi;
	private WarehouseRemote warehouse;
	
	@BeforeClass
	public static void setUpClass() throws NamingException {
	}
	
	@Before
	public void setUp() throws NamingException {
        jndi=new InitialContext();
        logger.debug("looking up jndi.name={}", warehouseJNDI);
	    warehouse = (WarehouseRemote)jndi.lookup(warehouseJNDI);
        
        logger.debug("{} rows deleted", warehouse.cleanup());
	}
	
	private List<Shipment> makeShipments() {
        List<Shipment> shipments = Arrays.asList(
                new Shipment("here"), new Shipment("there"), new Shipment("everywhere"));
        return shipments;
	}

	@Test
	public void required() throws UnexpectedState {
	    Product product=warehouse.createProductAndShipments(
	            new Product("thing", 6), makeShipments());
	    assertEquals("unexpected quantity for product", 3, product.getQuantity());
	}

    @Test
    public void notSupported() throws UnexpectedState {
        Product product=warehouse.createProductAndShipments_NotSupported(
                new Product("thing", 6), makeShipments());
        assertEquals("unexpected quantity for product", 3, product.getQuantity());
    }

    @Test
    public void requiresNew() throws UnexpectedState {
        Product product=warehouse.createProductAndShipments_RequiresNew(
                new Product("thing", 6), makeShipments());
        assertEquals("unexpected quantity for product", 3, product.getQuantity());
    }
    
    @Test
    public void async() throws UnexpectedState {
        Product product=warehouse.createProductAndShipments_Async(
                new Product("thing", 6), 3);
        assertEquals("unexpected quantity for product", 6, product.getQuantity());
        Product p2 = warehouse.getProduct(product.getId());
        for (int i=0; i<10 && p2.getQuantity() == 6; i++) {
            	try { Thread.sleep(1000); } catch (Exception ex){}
            	logger.info("waiting for async update");
            	p2 = warehouse.getProduct(product.getId());
        }
        assertEquals("product not updated async", 9, p2.getQuantity());
    }
    
    /**
     * This test demonstrates that a transaction and its resources get committed in 
     * the face of an application/checked exception. In this test the persisted
     * entity is passed in the exception and then verified to exist after the 
     * initial transaction is over.
     */
    @Test
    public void checkedException()   {
        Product product = null;
        try {
            warehouse.createProductAndThrowChecked(new Product("thing", 6));
            fail("planned exception not thrown");
        } catch (MyCheckedProductException ex) {
            //the *checked* exception contains the product in the exception 
            product = ex.getProduct();
            assertEquals("unexpected quantity for product", 6, product.getQuantity());
        } 
        //since we used a checked exception with no explicit call to setRollbackOnly()
        //our product should exist
        Product p2 = warehouse.getProduct(product.getId());
        assertEquals("product not updated async", 6, p2.getQuantity());
    }
    
    /**
     * This test demonstrates that a transaction and its resources gets rolled
     * back when an application/checked exception gets throw -- that was annotated
     * with rollback=true. In this case, the almost persisted product is passed
     * back in the exception but a follow-up check shows the product no longer 
     * exists.
     */
    @Test
    public void checkedRollbackException()   {
        Product product = null;
        try {
            warehouse.createProductAndThrowCheckedRollback(new Product("thing", 6));
            fail("planned exception not thrown");
        } catch (MyCheckedRollbackProductException ex) {
            //the *checked* exception contains the product in the exception 
            product = ex.getProduct();
            assertEquals("unexpected quantity for product", 6, product.getQuantity());
        } 
        //since we used a rollback=true checked exception -- our product will not be there
        Product p2 = warehouse.getProduct(product.getId());
        assertNull("unexpected/rolled back product found", p2);
    }
    
    @Test
    public void rollback()   {
        Product product = warehouse.createProductAndRollback(new Product("thing", 6));
        //since we did not throw an exception -- we get a normal return object
        assertEquals("unexpected quantity for product", 6, product.getQuantity());
        //since we setRollbackOnly() prior to the return -- our product will not be there
        Product p2 = warehouse.getProduct(product.getId());
        assertNull("unexpected/rolled back product found", p2);
    }
    
    @Test
    public void createProductBmt()   {
        Product product=warehouse.createProductBmt(new Product("thing", 6));
        assertEquals("unexpected quantity for product", 6, product.getQuantity());
        Product p2 = warehouse.getProduct(product.getId());
        assertEquals("product not updated async", 6, p2.getQuantity());
    }
    
}
