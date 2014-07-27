package myorg.relex;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.GregorianCalendar;

import myorg.relex.one2manybi.Borrower;
import myorg.relex.one2manybi.Car;
import myorg.relex.one2manybi.Loan;
import myorg.relex.one2manybi.Purchase;
import myorg.relex.one2manybi.SaleItem;
import myorg.relex.one2manybi.Tire;
import myorg.relex.one2manybi.TirePK;
import myorg.relex.one2manybi.TirePosition;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.*;

public class One2ManyBiTest extends JPATestBase {
    private static Log log = LogFactory.getLog(One2ManyBiTest.class);
    @Test
    public void testSample() {
        log.info("testSample");
    }
    
    /**
     * This test will verify the ability to create a one-to-many/many-to-one, bi-directional
     * relationship using a foreign key in the child entity table.
     */
    @Test
    public void testOneToManyBiFK() {
    	log.info("*** testOneToManyBiFK ***");
    	
    	log.debug("persisting parent");
    	Borrower borrower = new Borrower();
    	borrower.setName("fred");
    	em.persist(borrower);
    	em.flush();
    	
    	log.debug("persisting child");
    	Loan loan = new Loan(borrower);
    	borrower.getLoans().add(loan);
    	em.persist(borrower); //cascade.PERSIST
    	em.flush();
    	
    	log.debug("getting new instances from parent side");
    	em.detach(borrower);
    	Borrower borrower2 = em.find(Borrower.class, borrower.getId());
    	log.debug("checking parent");
    	assertNotNull("borrower not found", borrower2);
    	log.debug("checking parent collection");
    	assertEquals("no loans found", 1, borrower2.getLoans().size());
    	log.debug("checking child");
    	assertEquals("unexpected child id", loan.getId(), borrower2.getLoans().get(0).getId());
    	
    	log.debug("adding new child");
    	Loan loanB = new Loan(borrower2);
    	borrower2.getLoans().add(loanB);
    	em.persist(borrower2);
    	em.flush();

    	log.debug("getting new instances from child side");
    	em.detach(borrower2);
    	Loan loan2 = em.find(Loan.class, loan.getId());
    	log.debug("checking child");
    	assertNotNull("child not found", loan2);
    	assertNotNull("parent not found", loan2.getBorrower());
    	log.debug("checking parent");
    	assertEquals("unexpected number of children", 2, loan2.getBorrower().getLoans().size());
    	
    	log.debug("orphaning one of the children");
    	int startCount = em.createQuery("select count(l) from Loan l", Number.class).getSingleResult().intValue();
    	Borrower borrower3 = loan2.getBorrower();
    	borrower3.getLoans().remove(loan2);
    	em.flush();
    	assertEquals("orphaned child not deleted", startCount-1,
    			em.createQuery("select count(l) from Loan l", Number.class).getSingleResult().intValue());
    	
    	log.debug("deleting parent");
    	em.remove(borrower3);
    	em.flush();
    	assertEquals("orphaned child not deleted", startCount-2,
    			em.createQuery("select count(l) from Loan l", Number.class).getSingleResult().intValue());
    }

    /**
     * This test will verify the ability to create a one-to-many/many-to-one, bi-directional
     * relationship using a join-table mapped from the child side.
     */
    @Test
    public void testOneToManyBiJoinTable() {
    	log.info("*** testOneToManyBiJoinTable ***");
    	
    	log.debug("persisting parent");
    	Purchase purchase = new Purchase(new Date());
    	em.persist(purchase);
    	em.flush();
    	
    	log.debug("persisting child");
    	SaleItem item = new SaleItem(purchase);
    	item.setPrice(10.02);
    	purchase.addItem(item);
    	em.persist(purchase); //cascade.PERSIST
    	em.flush();
    	
    	log.debug("getting new instances");
    	em.detach(purchase);
    	Purchase purchase2 = em.find(Purchase.class, purchase.getId());
    	assertNotNull("parent not found", purchase2);
    	log.debug("checking parent");
    	assertTrue("unexpected date", purchase.getDate().equals(purchase2.getDate()));
    	log.debug("checking child");
    	assertEquals("unexpected number of children", 1, purchase2.getItems().size());
    	assertEquals("unepxtected child state", item.getPrice(), purchase2.getItems().get(0).getPrice(),.01);
    	log.debug("verify got new instances");
    	assertFalse("same parent instance returned", purchase == purchase2);
    	assertFalse("same child instance returned", item == purchase2.getItems().get(0));

    	log.debug("adding new child");
    	SaleItem itemB = new SaleItem(purchase2);
    	purchase2.addItem(itemB);
    	em.persist(purchase2);
    	em.flush();

    	log.debug("getting new instances from child side");
    	em.detach(purchase2);
    	SaleItem item2 = em.find(SaleItem.class, item.getId());
    	log.debug("checking child");
    	assertNotNull("child not found", item2);
    	assertNotNull("parent not found", item2.getPurchase());
    	log.debug("checking parent");
    	assertEquals("unexpected number of children", 2, item2.getPurchase().getItems().size());
    	
    	log.debug("orphaning one of the children");
    	int startCount = em.createQuery("select count(s) from SaleItem s", Number.class).getSingleResult().intValue();
    	Purchase purchase3 = item2.getPurchase();
    	purchase3.getItems().remove(item2);
    	em.flush();
    	assertEquals("orphaned child not deleted", startCount-1,
    			em.createQuery("select count(s) from SaleItem s", Number.class).getSingleResult().intValue());
    	
    	log.debug("deleting parent");
    	em.remove(purchase3);
    	em.flush();
    	assertEquals("orphaned child not deleted", startCount-2,
    			em.createQuery("select count(s) from SaleItem s", Number.class).getSingleResult().intValue());
    }
    
    /**
     * This method provides an example of a one-to-many/many-to-one, bi-directional 
     * relationship related through a compond primary key where a portion of the 
     * child primary key is derived from the parent primary key value.
     */
    @Test
    public void testOneToManyBiDerivedClass() {
    	log.info("*** testOneToManyBiDerivedClass ***");
    	
    	log.debug("persisting parent");
    	Car car = new Car();
    	car.setModel("DeLorean");
    	car.setYear(new GregorianCalendar(1983, 0, 0).getTime());
    	em.persist(car);
    	em.flush();
    	
    	log.debug("persisting child");
    	Tire tire = new Tire(car, TirePosition.RIGHT_FRONT);
    	tire.setMiles(2000);
    	car.getTires().add(tire);
    	em.persist(car); //cascade.PERSIST
    	em.flush();
    	
    	log.debug("getting new instances");
    	em.detach(car);
    	Car car2 = em.find(Car.class, car.getId());
    	assertNotNull("parent not found", car2);
    	log.debug("checking parent");
    	assertTrue("unexpected date", car.getYear().equals(car2.getYear()));
    	log.debug("checking child");
    	assertEquals("unexpected number of children", 1, car2.getTires().size());
    	assertEquals("unexpected child state", tire.getMiles(), car2.getTires().iterator().next().getMiles());
    	log.debug("verify got new instances");
    	assertFalse("same parent instance returned", car == car2);
    	assertFalse("same child instance returned", tire == car2.getTires().iterator().next());

    	log.debug("adding new child");
    	Tire tireB = new Tire(car2, TirePosition.LEFT_FRONT);
    	car2.getTires().add(tireB);
    	em.persist(car2);
    	em.flush();

    	log.debug("getting new instances from child side");
    	em.detach(car2);
    	Tire tire2 = em.find(Tire.class, new TirePK(car.getId(), tire.getPosition()));
    	log.debug("checking child");
    	assertNotNull("child not found", tire2);
    	assertNotNull("parent not found", tire2.getCar());
    	log.debug("checking parent");
    	assertEquals("unexpected number of children", 2, tire2.getCar().getTires().size());
    	
    	log.debug("orphaning one of the children");
    	int startCount = em.createQuery("select count(t) from Tire t", Number.class).getSingleResult().intValue();
    	Car car3 = tire2.getCar();
    	car3.getTires().remove(tire2);
    	em.flush();
    	assertEquals("orphaned child not deleted", startCount-1,
    			em.createQuery("select count(t) from Tire t", Number.class).getSingleResult().intValue());
    	
    	log.debug("deleting parent");
    	em.remove(car3);
    	em.flush();
    	assertEquals("orphaned child not deleted", startCount-2,
    			em.createQuery("select count(t) from Tire t", Number.class).getSingleResult().intValue());
    }
}