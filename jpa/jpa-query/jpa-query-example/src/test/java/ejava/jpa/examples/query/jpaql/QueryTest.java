package ejava.jpa.examples.query.jpaql;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import ejava.jpa.examples.query.Clerk;
import ejava.jpa.examples.query.Customer;
import ejava.jpa.examples.query.QueryBase;
import ejava.jpa.examples.query.Sale;
import ejava.jpa.examples.query.Store;

public class QueryTest extends QueryBase {
	private static final Log log = LogFactory.getLog(QueryTest.class);
	
	@Test
    public void testSingleResult() {
        log.info("*** testSingleResult() ***");
        
        TypedQuery<Store> query = em.createQuery(
                "select s from Store s where s.name='Big Al''s'", Store.class);
        Store store = query.getSingleResult();
        assertNotNull("store is null", store);
        log.info("found store:" + store);
        
        boolean noResult = false;
        try {
            store = em.createQuery(
               "select s from Store s where s.name='A1 Sales'", Store.class)
               .getSingleResult();
        }
        catch (NoResultException ex) {
            log.debug("expected exception thrown:" + ex);
            noResult = true;
        }
        assertTrue("NoResultExcetion not thrown", noResult);
        
        boolean nonUniqueResult = false;
        try {
        	Clerk clerk = em.createQuery(
                "select c from Clerk c where lastName='Pep'", Clerk.class)
         		.getSingleResult();
            log.info("found clerk:" + clerk);
        }
        catch (NonUniqueResultException ex) {
            log.info("expected exception thrown:" + ex);
            nonUniqueResult = true;
        }
        assertTrue("NonUniqueResultException not thrown", nonUniqueResult);
    }

    @Test
    public void testResultList() {
        log.info("*** testResultList() ***");
        
        TypedQuery<Clerk> query = em.createQuery(
            "select c from Clerk c where lastName='Pep'", Clerk.class);
        List<Clerk> clerks = query.getResultList();
        assertTrue("unexpected number of clerks:" + clerks.size(), 
            clerks.size() > 1);
        for(Clerk c : clerks) {
            log.info("found clerk:" + c);
        }        
    }
    
    @Test
    public void testParameters() {
        log.info("*** testParameters() ***");
        
        TypedQuery<Customer> query = em.createQuery(
                "select c from Customer c " +
                "where c.firstName=:firstName and c.lastName=:lastName",
                Customer.class);
        query.setParameter("firstName", "cat");
        query.setParameter("lastName", "inhat");
        
        Customer customer = query.getSingleResult();
        assertNotNull(customer);
        log.info("found customer for param names:" + customer);
        
        query = em.createQuery(
                "select c from Customer c " +
                "where c.firstName=?1 and c.lastName like ?2", Customer.class);
        query.setParameter(1, "thing");
        query.setParameter(2, "%");
        List<Customer> customers = query.getResultList();
        assertTrue("unexpected number of customers:" + customers.size(),
                customers.size() == 2);
        for(Customer c : customers) {
            log.info("found customer for param position:" + c);
        }
    }
    
    @Test
    public void testDateParameter() {
        log.info("*** testDateParameter() ***");
        
        log.info(em.createQuery("select c from Clerk c", Clerk.class).getResultList());
        
        Calendar hireDate = new GregorianCalendar(1972, Calendar.JANUARY, 1);
        TypedQuery<Clerk> query = em.createQuery(
                "select c from Clerk c " +
                "where c.hireDate > :date", Clerk.class);
        query.setParameter("date", hireDate.getTime(), TemporalType.DATE);
        
        Clerk clerk = query.getSingleResult();
        log.info("found clerk by date(" + hireDate.getTime() + "):" + clerk);
    }
    
    @Test
    public void testPaging() {
        log.info("*** testPaging() ***");
        
        TypedQuery<Sale> query = em.createQuery(
                "select s from Sale s", Sale.class);
        for(int i=0; i<2; i++) {
            List<Sale> sales = query.setMaxResults(10)
                                    .setFirstResult(i)
                                    .getResultList();
            assertTrue("unexpected sale count:" + sales.size(),
                    sales.size() >= 1);
            for(Sale s: sales) {
                log.info("found sale in page(" + i + "):" + s);
                em.detach(s); //we are done with this
            }
        }
    }
    
    @Test
    public void testLock() {
        log.info("*** testLock() ***");
        //get a list of clerks to update -- locked so others cannot change
        List<Clerk> clerks = em.createQuery(
        		"select c from Clerk c " +
        		"where c.hireDate > :date", Clerk.class)
        		.setParameter("date", new GregorianCalendar(1972,Calendar.JANUARY,1).getTime())
        		.setLockMode(LockModeType.PESSIMISTIC_WRITE)
        		.setHint("javax.persistence.lock.timeout", 0)
        		.getResultList(); 
        //make changes
        for (Clerk c: clerks) {
        	c.setHireDate(new GregorianCalendar(1972, Calendar.FEBRUARY, 1).getTime());
        }
    }
}
