package ejava.examples.orm.ejbql;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import ejava.examples.orm.ejbql.annotated.Clerk;

public class BulkDemo extends DemoBase {
    
    @SuppressWarnings("unchecked")
    @Test
    public void testBulkUpdate() {
        log.info("*** testBulkUpdate() ***");
        
        Query query = em.createQuery(
                "select c from Clerk c where c.lastName=:last");
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
