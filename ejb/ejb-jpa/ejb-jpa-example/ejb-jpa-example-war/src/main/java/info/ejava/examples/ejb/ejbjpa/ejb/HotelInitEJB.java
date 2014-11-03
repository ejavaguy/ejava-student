package info.ejava.examples.ejb.ejbjpa.ejb;

import info.ejava.examples.ejb.ejbjpa.dao.JPAHotelDAO;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The following EJB provides an example of an EJB using BEAN-managed transactions. 
 */
@Singleton
@Startup
@TransactionManagement(TransactionManagementType.BEAN)
public class HotelInitEJB implements HotelInitRemote {
    private static final Logger logger = LoggerFactory.getLogger(HotelInitEJB.class);
    
    /**
     * We will still get a resource from the container -- except in this case we
     * will get an EMF rather than an EntityManager. Note the different @PersistenceUnit
     * annotation as well.
     */
    @PersistenceUnit(unitName="ejbjpa-hotel")
    private EntityManagerFactory emf;
    
    @EJB
    HotelMgmtLocal hotelMgmt;
    
    /**
     * We get a UTx injected to control our overall JTA transaction.
     */
    @Resource
    private UserTransaction tx;
    
    @PostConstruct
    public void init() {
        logger.debug("*** HotelInit:init({})", super.hashCode());
        clearAll();
        populate();
        logger.debug("we have {} floors", hotelMgmt.getFloors(0, 0).size());
    }
    
    @Override
    public void clearAll() {
        logger.debug("clearing ejbjpa hotel");
        EntityManager em=emf.createEntityManager();
        try {
            JPAHotelDAO dao = new JPAHotelDAO();
            dao.setEntityManager(em);
            tx.begin();
            em.joinTransaction(); //tells the EM to join the JTA UTx we are managing
            dao.clearAll();
            tx.commit();
            logger.debug("clearing complete");
        } catch (Exception ex) {
            try {
                tx.rollback();
                logger.debug("error clearing hotel", ex);
            } catch (Exception ex2) {
                throw new EJBException(ex2);
            }
            finally {}
        } finally {
            if (em!=null) {
                em.close();
            }
        }
    }

    @Override
    public void populate() {
        logger.debug("populating ejbjpa hotel");
        EntityManager em=emf.createEntityManager();
        try {
            JPAHotelDAO dao = new JPAHotelDAO();
            dao.setEntityManager(em);
            tx.begin();
            em.joinTransaction(); //tells the EM to join the JTA UTx we are managing
            dao.populate();
            tx.commit();
            logger.debug("populate complete");
        } catch (Exception ex) {
            try {
                tx.rollback();
                logger.debug("error populating hotel", ex);
            } catch (Exception ex2) {
                throw new EJBException(ex2);
            }
            finally {}
        } finally {
            if (em!=null) {
                em.close();
            }
        }
    }
}
