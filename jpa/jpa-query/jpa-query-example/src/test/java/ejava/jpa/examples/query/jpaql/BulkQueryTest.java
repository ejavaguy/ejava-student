package ejava.jpa.examples.query.jpaql;

import static org.junit.Assert.*;


import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import ejava.jpa.examples.query.Clerk;
import ejava.jpa.examples.query.QueryBase;

public class BulkQueryTest extends QueryBase {
	private static final Logger log = LoggerFactory.getLogger(BulkQueryTest.class);
    
    @Test
    public void testBulkUpdate() {
        log.info("*** testBulkUpdate() ***");
        
        TypedQuery<Clerk> query = em.createQuery(
                "select c from Clerk c where c.lastName=:last", Clerk.class);
        List<Clerk> clerks = query.setParameter("last", "Pep").getResultList();
        assertTrue("no clerks found", clerks.size() > 0);
        for (Clerk c: clerks) {
            log.info("clerk found before update:" + c);
        }
        
        Query update = em.createQuery(
                "update Clerk c set c.lastName=:newlast where c.lastName=:last");
        update.setParameter("last", "Pep");
        update.setParameter("newlast", "Peppy");
        int rows = update.executeUpdate();
        assertEquals("unexpected rows updated:" + rows, clerks.size(), rows);
        
        clerks = query.getResultList();
        assertEquals("unexpected number of clerks:" + clerks.size(), 
                0, clerks.size());
        em.flush();
        em.clear(); //need to clear cache so see changes made in DB
        
        clerks = query.setParameter("last", "Peppy").getResultList();
        for (Clerk c: clerks) {
            log.info("clerk found after update:" + c);
        }
    }
    
    
    @Test
    public void testBulkDelete() {
        log.info("*** testBulkDelete() ***");
        
        Query query = em.createQuery(
                "select count(c) from Customer c " +
                "where c.firstName like :first AND c.lastName like :last");
        long custCount = (Long)query.setParameter("first", "thing")
                                    .setParameter("last", "%")
                                    .getSingleResult();
        assertTrue("no customers found", custCount > 0);
        
        Query update = em.createQuery(
                "delete from Customer c " +
                "where c.firstName like :first AND c.lastName like :last");
        int rows = update.setParameter("first", "thing")
                         .setParameter("last", "%")
                         .executeUpdate();
        assertTrue("no rows updated", rows > 0);
        em.flush();
        em.clear();

        custCount = (Long)query.setParameter("first", "thing")
                               .setParameter("last", "%")
                               .getSingleResult();
        assertEquals("customers found:" + custCount, 0, custCount);
    }
}
