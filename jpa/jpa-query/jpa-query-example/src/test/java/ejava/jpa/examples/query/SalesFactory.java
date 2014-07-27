package ejava.jpa.examples.query;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import javax.persistence.EntityManager;

public class SalesFactory {
	private EntityManager em;
	
	public SalesFactory setEntityManager(EntityManager em) {
		this.em = em;
		return this;
	}
	
    protected void populate() {
        Store store = new Store().setName("Big Al's");
        em.persist(store);

        Clerk manny = new Clerk()
        	.setFirstName("Manny")
        	.setLastName("Pep")
        	.setHireDate(new GregorianCalendar(1970,Calendar.JANUARY, 1).getTime());

        Clerk moe = new Clerk()
        	.setFirstName("Moe")
        	.setLastName("Pep")
        	.setHireDate(new GregorianCalendar(1970,Calendar.JANUARY, 1).getTime());

        Clerk jack = new Clerk()
        	.setFirstName("Jack")
        	.setLastName("Pep")
        	.setHireDate(new GregorianCalendar(1973,Calendar.MARCH, 1).getTime());

        em.persist(manny);
        em.persist(moe);
        em.persist(jack);
        
        Customer cat = new Customer()
        	.setFirstName("cat")
            .setLastName("inhat");
        
        Customer one = new Customer()
        	.setFirstName("thing")
        	.setLastName("one");
        
        Customer two = new Customer()
        	.setFirstName("thing")
        	.setLastName("two");
        
        em.persist(cat);
        em.persist(one);
        em.persist(two);
        
        Sale sale1 = new Sale();
        sale1.setAmount(new BigDecimal(100.00))
        	.setDate(new GregorianCalendar(1998,03,10,10, 13, 35).getTime())
        	.setStore(store)
        	.setBuyerId(cat.getId())
        	.addClerk(manny);
        manny.addSale(sale1);
        store.addSale(sale1);
        
        Sale sale2 = new Sale()
        	.setAmount(new BigDecimal(150.00))
        	.setDate(new GregorianCalendar(1999,05,11,14, 15, 10).getTime())
        	.setStore(store)
        	.setBuyerId(one.getId())
        	.addClerk(manny, moe);
        manny.addSale(sale2);
        moe.addSale(sale2);
        store.addSale(sale2);
        
        em.persist(sale1);
        em.persist(sale2);
    }

	public void cleanup() {
        //Store has cascade=DELETE to Sale
        Collection<Store> stores = 
            em.createQuery("select s from Store s", Store.class).getResultList();
        for(Store s : stores) {
            em.remove(s);
        }
        em.createQuery("delete from Customer c").executeUpdate();
        em.createQuery("delete from Clerk c").executeUpdate();        
	}
}
