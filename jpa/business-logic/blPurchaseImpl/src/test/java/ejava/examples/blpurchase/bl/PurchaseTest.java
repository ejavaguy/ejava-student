package ejava.examples.blpurchase.bl;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ejava.examples.blpurchase.bo.Account;
import ejava.examples.blpurchase.bo.Product;

/**
 * This test verifies the top-level requirements our purchase application.
 */
public class PurchaseTest {
	private static final Logger log = LoggerFactory.getLogger(PurchaseTest.class);

	private Catalog catalog;
	private Purchasing purchasing;
	
	private String validEmail;
	private String validPassword;

	/**
	 * A user shall be able to establish an account with just a first 
	 * and last name and a unique email address.
	 */
	@Test
	public void establishAccount() {
		log.info("*** establishAccount ***");
		
		//the user will supply their email address, first and last name
		String email="jharb@ravens.com";
		String firstName="john";
		String lastName="harbaugh";
		Account account = purchasing.createAccount(email, firstName, lastName);
		
		//they will get back a generated password to use as a login for the account
		assertNotNull("no account returned", account);
		assertNotNull("no password assigned", account.getPassword());
	}
	
	/**
	 * A user shall be able browse products in the catalog.
	 */
	@Test
	public void browseCatalog() {
		log.info("*** browseCatalog ***");
		
			//the user will ask for product summaries in pages
		int pageSize=10;
		int offset=0;
		List<Product> products = catalog.getProducts(offset, pageSize);
		
			//they will receive <= a page size of product information
		assertNotNull("no products returned", products);
		assertTrue("no products provided", products.size() > 0);
		
			//they can page thru the entire set
		for (int i=0; products.size() != 0; i++) {
			offset += products.size();
			products = catalog.getProducts(offset, pageSize);
			assertTrue("this catalog never ends!!!", i<100);
		}
	}
	
	/**
	 * A user shall be able to purchase a product in the catalog.
	 */
	@Test
	public void purchaseProduct() {
		log.info("*** purchaseProduct ***");
		
			//the user selects a product
		Product product=null;
		Random random=new Random();
		for (int i=0; product == null || product.getCount()==0; i++) {
			List<Product> products=catalog.getProducts(random.nextInt(100), 1);
			product=products.iterator().next();
			assertTrue("I can't find anything to buy!!!", i<1000);
		}
		
			//the user adds the product to their shopping cart by providing the 
		    //product id and their credentials
		int count=catalog.addToCart(product.getId(), validEmail);
			//the user receives a count of the items in the cart
		assertEquals("somebody tweeked my cart!!!!", 1, count);
		
			//the user checks out with the cashier -- payment not yet implemented
		double total=purchasing.checkout(validEmail, validPassword);
		
			//the user gets a total amount back as their receipt
		assertEquals("price doesn't add up", product.getPrice(), total, .01);
	}

	private static PurchasingFactory factory;
	@BeforeClass
	public static void setUpClass() {
		factory = new PurchasingFactory();
	}
	
	@Before
	public void setUp() {
		factory.init();
		catalog=factory.getCatalog();
		purchasing=factory.getPurchasing();
		
			//seed system with products
		factory.createProducts();
		
			//seed system with an account
		Account account = factory.createAccount();
		validEmail=account.getEmail();
		validPassword=account.getPassword();
		
			//start a transaction for test method
		factory.getEntityManager().getTransaction().begin();
	}
	
	@After
	public void tearDown() {
		factory.close(); //close impl will commit or rollback
	}
}
