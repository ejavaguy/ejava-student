package ejava.jpa.examples.query.criteria;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import ejava.jpa.examples.query.Clerk;
import ejava.jpa.examples.query.Customer;
import ejava.jpa.examples.query.QueryBase;

public class BulkQueryTest extends QueryBase {
	private static final Logger log = LoggerFactory.getLogger(BulkQueryTest.class);
    
    @Test
    public void testBulkUpdate() {
        log.info("*** testBulkUpdate() ***");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        //"select c from Clerk c where c.lastName=:last"
        CriteriaQuery<Clerk> qdef = cb.createQuery(Clerk.class);
        Root<Clerk> c = qdef.from(Clerk.class);
        ParameterExpression<String> lastName = cb.parameter(String.class);
        qdef.select(c)
            .where(cb.equal(c.get("lastName"), lastName));

        TypedQuery<Clerk> query = em.createQuery(qdef);
        List<Clerk> clerks = query.setParameter(lastName, "Pep").getResultList();
        assertTrue("no clerks found", clerks.size() > 0);
        for (Clerk clerk: clerks) {
            log.info("clerk found before update:" + clerk);
        }

        //"update Clerk c set c.lastName=:newlast where c.lastName=:last"
        CriteriaUpdate<Clerk> qdef2=cb.createCriteriaUpdate(Clerk.class);
        Root<Clerk> c2 = qdef2.from(Clerk.class);
        qdef2.set("lastName", "Peppy")
             .where(cb.equal(c2.get("lastName"), "Pep"));
        
        Query update = em.createQuery(qdef2);
        int rows = update.executeUpdate();
        assertEquals("unexpected rows updated:" + rows, clerks.size(), rows);
        
        clerks = query.getResultList();
        assertEquals("unexpected number of clerks:" + clerks.size(), 
                0, clerks.size());
        em.flush();
        em.clear(); //need to clear cache so see changes made in DB
        
        clerks = query.setParameter(lastName, "Peppy").getResultList();
        for (Clerk clerk: clerks) {
            log.info("clerk found after update:" + clerk);
        }
    }
    
    
    @Test
    public void testBulkDelete() {
        log.info("*** testBulkDelete() ***");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        
        //"select count(c) from Customer c " +
        //"where c.firstName like :first AND c.lastName like :last");
        CriteriaQuery<Long> qdef = cb.createQuery(Long.class);
        Root<Customer> c = qdef.from(Customer.class);
        ParameterExpression<String> first = cb.parameter(String.class);
        ParameterExpression<String> last = cb.parameter(String.class);
        qdef.select(cb.count(c))
        	.where(cb.and(
	        			cb.like(c.<String>get("firstName"), first),
	        			cb.like(c.<String>get("lastName"), last)
        				)
        			);
        
        Query query = em.createQuery(qdef);
        long custCount = (Long)query.setParameter(first, "thing")
                                    .setParameter(last, "%")
                                    .getSingleResult();
        assertTrue("no customers found", custCount > 0);
        
        //"delete from Customer c " +
        //"where c.firstName like :first AND c.lastName like :last");
        CriteriaDelete<Customer> delete = cb.createCriteriaDelete(Customer.class);
		delete.from(Customer.class);
        delete.where(cb.and(
    			cb.like(c.<String>get("firstName"), "thing"),
    			cb.like(c.<String>get("lastName"), "%")
        		));

        Query update = em.createQuery(delete);
        int rows = update.executeUpdate();
        assertTrue("no rows updated", rows > 0);
        em.flush();
        em.clear();

        custCount = (Long)query.setParameter(first, "thing")
                               .setParameter(last, "%")
                               .getSingleResult();
        assertEquals("customers found:" + custCount, 0, custCount);
    }
}
