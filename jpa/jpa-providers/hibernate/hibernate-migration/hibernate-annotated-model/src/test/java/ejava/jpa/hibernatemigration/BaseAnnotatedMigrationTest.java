package ejava.jpa.hibernatemigration;

import static org.junit.Assert.assertEquals;



import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import ejava.jpa.hibernatemigration.annotated.Clerk;
import ejava.jpa.hibernatemigration.annotated.Customer;
import ejava.jpa.hibernatemigration.annotated.CustomerLevel;
import ejava.jpa.hibernatemigration.annotated.Sale;

public abstract class BaseAnnotatedMigrationTest {
	private final Log log = LogFactory.getLog(getClass());

	protected abstract void save(Object entity);
	protected abstract void flush();
	protected abstract void clear();
	protected abstract <T> T get(Class<T> clazz, Serializable pk);
	protected abstract void beginTransaction();
	protected abstract void commitTransaction();

	@Test
	public void testPersist() {
		log.info("*** testPersist ***");

		//create our customer
		Customer customer = new Customer();
		customer.setName("joe");
		customer.setEmail("joe@email.com");
		customer.setLevel(CustomerLevel.SILVER);
		
		//create two clerks for the sale
		Clerk clerk1 = new Clerk();
		clerk1.setName("tom");
		clerk1.setHireDate(new GregorianCalendar(2010, Calendar.JANUARY, 1).getTime());
		
		//create the sale
		Clerk clerk2 = new Clerk();
		clerk2.setName("mary");
		clerk2.setHireDate(new GregorianCalendar(2012, Calendar.JULY, 1).getTime());

		Sale sale = new Sale();
		sale.setDateTime(new Date());
		sale.setAmount(new BigDecimal(100.12));
		
		//associate the entities
		sale.setCustomer(customer);
		customer.setPurchaes(sale);
		sale.addSalesClerk(clerk1);
		clerk1.addSale(sale);
		sale.addSalesClerk(clerk2);
		clerk2.addSale(sale);

		//persist objects
		log.info("(prior to persist) sale=" + sale);
		save(customer);
		save(clerk1);
		save(clerk2);
		save(sale);

		//flushing session to database
		flush();
		log.info("(after flush) sale=" + sale);
		
		//get a new instance of sale
		clear();
		Sale sale2 = get(Sale.class, sale.getId());
		
		//verify state and relationships were persisted
		assertNotNull("could not locate sale", sale2);
		assertEquals("unexpected amount", sale.getAmount().intValue(), sale2.getAmount().intValue());
		
		//test the sale/customer many-to-one collection mapping from owning side
		assertNotNull("no customer found", sale2.getCustomer());
		assertEquals("unexpected sale.customer.id", sale.getCustomer().getId(), sale2.getCustomer().getId());
		assertEquals("unexpected customer name", customer.getName(), sale2.getCustomer().getName());
		
		//test the sale/customer many-to-one collection mapping from inverse side
		assertEquals("unexpected purchase count", 1, sale2.getCustomer().getPurchases().size());
		assertEquals("unexpected saleId", sale.getId(), sale2.getCustomer().getPurchases().iterator().next().getId());

		//test the sale/clerk many-to-many collection from owning side 
		assertEquals("unexpected number of clerks", 2, sale2.getSalesClerks().size());
		assertTrue("could not locate clerk1", sale2.getSalesClerks().contains(clerk1));
		assertTrue("could not locate clerk2", sale2.getSalesClerks().contains(clerk2));
		
		//test the sale/clerk many-to-many collection from inverse side
		for (Clerk clerk: sale2.getSalesClerks()) {
			assertEquals("unexpected number if sales", 1, clerk.getSales().size());
			assertTrue("sale not found in clerk", clerk.getSales().contains(sale));
		}
	}
}