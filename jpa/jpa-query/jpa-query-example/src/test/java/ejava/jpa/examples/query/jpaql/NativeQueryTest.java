package ejava.jpa.examples.query.jpaql;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import ejava.jpa.examples.query.Customer;
import ejava.jpa.examples.query.QueryBase;

public class NativeQueryTest extends QueryBase {
	private static final Logger log = LoggerFactory.getLogger(NativeQueryTest.class);

    @SuppressWarnings("unchecked")
    @Test
    public void testScalarNativeQuery() {
        log.info("*** testSimpleNativeQuery() ***");
        
        Table table = Customer.class.getAnnotation(Table.class);
        
        Query query = em.createNativeQuery(
        	String.format("select * from %s ", table.name()) +
            String.format("where %s.FIRST_NAME = :first", table.name()));
        List<Object[]> results = query.setParameter("first", "thing")
        		                      .getResultList();
        assertTrue("no customers found", results.size() > 0);
        for(Object[] o: results) {
            log.info("results=" + Arrays.toString(o));
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testEntityNativeQuery() {
        log.info("*** testEntityNativeQuery() ***");

        Table table = Customer.class.getAnnotation(Table.class);
        
        Query query = em.createNativeQuery(
        	String.format("select * from %s ", table.name()) +
            String.format("where %s.FIRST_NAME = :first", table.name()), Customer.class);
        List<Customer> results = query.setParameter("first", "thing")
                                      .getResultList();
        assertTrue("no customers found", results.size() > 0);
        for(Customer c: results) {
            log.info("customer found:" + c);
            log.info("em.contains(" + em.contains(c) + ")");
            assertTrue(em.contains(c));
        }
    }
}
