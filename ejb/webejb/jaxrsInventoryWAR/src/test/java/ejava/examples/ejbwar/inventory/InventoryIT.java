package ejava.examples.ejbwar.inventory;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ejava.examples.ejbwar.customer.bo.Customer;
import ejava.examples.ejbwar.customer.bo.Customers;
import ejava.examples.ejbwar.customer.client.CustomerClient;
import ejava.examples.ejbwar.inventory.bo.Categories;
import ejava.examples.ejbwar.inventory.bo.Category;
import ejava.examples.ejbwar.inventory.bo.Product;
import ejava.examples.ejbwar.inventory.bo.Products;
import ejava.examples.ejbwar.inventory.client.InventoryClient;

/**
 * This class implements a JAX-RS based integration test with the 
 * inventory application deployed to the server.
 */
public class InventoryIT {
	private static final Log log = LogFactory.getLog(InventoryIT.class);
	private InventoryClient inventoryClient;
	private CustomerClient customerClient;
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		//give application time to fully deploy
		if (Boolean.parseBoolean(System.getProperty("cargo.startstop", "false"))) {
			long waitTime=15000;
	    	log.info(String.format("pausing %d secs for server deployment to complete", waitTime/1000));
	    	Thread.sleep(waitTime);
		}
	}

	@Before
	public void setUp() throws Exception {
		InventoryTestConfig config = new InventoryTestConfig("/it.properties");
		log.info("uri=" + config.appURI());
		inventoryClient = config.inventoryClient();
		customerClient = config.customerClient();
		cleanup();
	}
	
	/**
	 * Remove data from previous tests
	 */
	public void cleanup() throws Exception {
		Categories categories = inventoryClient.findCategoryByName("", 0, 0);
		assertNotNull("error getting categories", categories);
		log.info(String.format("deleting %d categories", categories.getCategories().size()));
		for (Category c: categories.getCategories()) {
			inventoryClient.deleteCategory(c.getId());
		}
		assertEquals("unexpected categories after cleanup", 
				0, 
				inventoryClient.findCategoryByName("", 0, 0).getCategories().size());
		
		Products products = inventoryClient.findProductsByName("", 0, 0);
		assertNotNull("error getting products", categories);
		log.info(String.format("deleting %d products", products.getProducts().size()));
		for (Product p: products.getProducts()) {
			inventoryClient.deleteProduct(p.getId());
		}
		assertEquals("unexpected products after cleanup", 
				0, 
				inventoryClient.findProductsByName("", 0, 0).getProducts().size());
		
		Customers customers = customerClient.findCustomersByName("", "", 0, 0);
		assertNotNull("error getting customers", customers);
		log.info(String.format("deleting %d customers", customers.getCustomers().size()));
		for (Customer c: customers.getCustomers()) {
			customerClient.deleteCustomer(c.getId());
		}
		assertEquals("unexpected customers after cleanup", 
				0, 
				customerClient.findCustomersByName("", "", 0, 0).getCustomers().size());
	}

	@Test
	public void testAddInventory() throws Exception {
		log.info("*** testAddInventory() ***");
		
		//create a new product
		Product product = new Product("chips");		
		product = inventoryClient.createProduct(product, "snacks");
		assertNotNull("product not created", product);
		log.info("created product:" + product);
		assertTrue("product primary ket not assigned", product.getId()>0);
		
		//get that product by ID
		Product p2 = inventoryClient.getProduct(product.getId());
		assertNotNull("product not found", p2);
		
		//get the category summary we assigned the product
		Categories categories = inventoryClient.findCategoryByName("snacks",0,0);
		assertNotNull("error getting categories", categories);
		assertEquals("category not found", 1, categories.getCategories().size());
		
		//get the full category
		Category category = inventoryClient.getCategory(categories.getCategories().get(0).getId());
		assertNotNull("unable to find category", category);
		log.info(category);
		
		//verify category has product
		assertEquals("unexpected product count", 1, category.getProductCount());
		assertEquals("unexpected products", 1, category.getProducts().size());
		Product p = category.getProducts().get(0);
		assertEquals("unexpected product name in category", product.getName(), p.getName());
	}
	
	@Test
	public void testUpdateProduct() throws Exception {
		log.info("*** testUpdateProduct ***");
		
		//create a new product
		Product product = new Product("mp3");		
		product = inventoryClient.createProduct(product, "radios");
		assertNotNull("product not created", product);
		log.info("created product:" + product);
		assertTrue("product primary ket not assigned", product.getId()>0);
		
		//update product
		product.setPrice(3.00);
		product.setQuantity(987);
		Product p2 = inventoryClient.updateProduct(product);
		assertNotNull("update failed", p2);
		assertEquals("unexpected price", product.getQuantity(), p2.getQuantity());
		assertEquals("unexpected price", product.getPrice(), p2.getPrice(), .01);
	}
	
	@Test
	public void testManageCustomer() throws Exception {
		log.info("*** testManageCustomer ***");
	
		//create customer
		Customer customer = new Customer("cat", "inhat");
		customer = customerClient.addCustomer(customer);
		
		//find the customer
		Customers customers = customerClient.findCustomersByName("cat", "", 0, 0);
		assertEquals("unexpected customer", 1, customers.getCustomers().size());
		
		//get the customer
		Customer c2 = customerClient.getCustomer(customers.getCustomers().get(0).getId());
		assertEquals("unexpected firstName", customer.getFirstName(), c2.getFirstName());
		assertEquals("unexpected lastName", customer.getLastName(), c2.getLastName());
		
		//create additional customers
		customerClient.addCustomer(new Customer("thing","one"));
		customerClient.addCustomer(new Customer("thing","two"));
		
		//verify they can be found
		assertEquals("unexpected customer", 2, 
			customerClient.findCustomersByName("thing", "", 0, 0).getCustomers().size());
		assertEquals("unexpected customer", 1, 
			customerClient.findCustomersByName("thing", "one", 0, 0).getCustomers().size());
		assertEquals("unexpected customer", 1, 
			customerClient.findCustomersByName("", "two", 0, 0).getCustomers().size());
	}

}
