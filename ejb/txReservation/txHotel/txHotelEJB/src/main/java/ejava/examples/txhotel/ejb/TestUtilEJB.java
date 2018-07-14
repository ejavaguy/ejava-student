package ejava.examples.txhotel.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This EJB provides helper methods used by remote test clients.
 */
@Stateless
public class TestUtilEJB implements TestUtilRemote {
    private static final Logger log = LoggerFactory.getLogger(TestUtilEJB.class);

    @PersistenceContext(unitName="txhotel")
    private EntityManager em;

    @Override
	public void reset() {
    	log.info("resetting the hotel DB tables");
    	em.createQuery("delete from Reservation").executeUpdate();
    	em.createQuery("delete from Person").executeUpdate();
	}

}
