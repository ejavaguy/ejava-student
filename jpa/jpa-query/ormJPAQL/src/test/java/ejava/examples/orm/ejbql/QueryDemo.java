package ejava.examples.orm.ejbql;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.junit.Test;

import ejava.examples.orm.ejbql.annotated.Clerk;
import ejava.examples.orm.ejbql.annotated.Customer;
import ejava.examples.orm.ejbql.annotated.Sale;
import ejava.examples.orm.ejbql.annotated.Store;

public class QueryDemo extends DemoBase {
	@Test
    public void testSingleResult() {
        log.info("*** testSingleResult() ***");
        
        Query query = em.createQuery(
                "select s from Store s where s.name='Big Al''s'");
        Store store = (Store)query.getSingleResult();
        assertNotNull("store is null", store);
        log.info("found store:" + store);
        
        boolean noResult = false;
        try {
            query = em.createQuery(
               "select s from Store s where s.name='A1 Sales'");
            store = (Store)query.getSingleResult();
        }
        catch (NoResultException ex) {
            log.debug("expected exception thrown:" + ex);
            noResult = true;
        }
        assertTrue("NoResultExcetion not thrown", noResult);
        
        boolean nonUniqueResult = false;
        try {
            query = em.createQuery(
                    "select c from Clerk c where lastName='Pep'");
            Clerk clerk = (Clerk)query.getSingleResult();
            log.info("found clerk:" + clerk);
        }
        catch (NonUniqueResultException ex) {
            log.info("expected exception thrown:" + ex);
            nonUniqueResult = true;
        }
        assertTrue("NonUniqueResultException not thrown", nonUniqueResult);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testResultList() {
        log.info("*** testResultList() ***");
        
        Query query = em.createQuery(
            "select c from Clerk c where lastName='Pep'");
        List<Clerk> clerks = query.getResultList();
        assertTrue("unexpected number of clerks:" + clerks.size(), 
            clerks.size() > 1);
        for(Clerk c : clerks) {
            log.info("found clerk:" + c);
        }        
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testParameters() {
        log.info("*** testParameters() ***");
        
        Query query = em.createQuery(
                "select c from Customer c " +
                "where c.firstName=:firstName and c.lastName=:lastName");
        query.setParameter("firstName", "cat");
        query.setParameter("lastName", "inhat");
        
        Customer customer = (Customer)query.getSingleResult();
        assertNotNull(customer);
        log.info("found customer for param names:" + customer);
        
        query = em.createQuery(
                "select c from Customer c " +
                "where c.firstName=?1 and c.lastName like ?2");
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
        
        Calendar hireDate = Calendar.getInstance();
        hireDate.set(Calendar.YEAR, 1972);
        Query query = em.createQuery(
                "select c from Clerk c " +
                "where c.hireDate > :date");
        query.setParameter("date", hireDate.getTime(), TemporalType.DATE);
        
        Clerk clerk = (Clerk)query.getSingleResult();
        log.info("found clerk by date(" + hireDate.getTime() + "):" + clerk);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testPaging() {
        log.info("*** testPaging() ***");
        
        Query query = em.createQuery(
                "select s from Sale s");
        for(int i=0; i<2; i++) {
            List<Sale> sales = query.setMaxResults(10)
                                    .setFirstResult(i)
                                    .getResultList();
            assertTrue("unexpected sale count:" + sales.size(),
                    sales.size() >= 1);
            for(Sale s: sales) {
                log.info("found sale in page(" + i + "):" + s);
            }
            em.clear();
        }
    }
}
