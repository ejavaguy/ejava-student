package info.ejava.examples.ejb.ejbjpa.ejb;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Singleton;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.ejava.examples.ejb.ejbjpa.dao.JPAHotelDAO;

/**
 * The following EJB provides an example of an EJB using BEAN-managed transactions. 
 */
@Singleton
@TransactionManagement(TransactionManagementType.BEAN) //RESOURCE_LOCAL requires BMT!
public class HotelInitResourceLocalEJB implements HotelInitRemote {
    private static final Logger logger = LoggerFactory.getLogger(HotelInitResourceLocalEJB.class);
    
    /**
     * We will still get a resource from the container -- except in this case we
     * will get an EMF rather than an EntityManager. Note the different @PersistenceUnit
     * annotation as well.
     */
    @PersistenceUnit(unitName="ejbjpa-hotel-rl")
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
        logger.debug("*** HotelBmtInit:init({})", super.hashCode());
    }
    
    @Override
    public void clearAll() {
        logger.debug("clearing ejbjpa hotel");
        EntityManager em=emf.createEntityManager();
        try {
            JPAHotelDAO dao = new JPAHotelDAO();
            dao.setEntityManager(em);
                //tx.begin();
                //em.joinTransaction(); //tells the EM to join the JTA UTx we are managing
            em.getTransaction().begin();   //we are using RESOURCE_LOCAL!
            dao.clearAll();
                //tx.commit();
            em.getTransaction().commit();
            logger.debug("clearing complete");
        } catch (Exception ex) {
            try {
                    //tx.rollback();
                em.getTransaction().rollback();
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
//            tx.begin();
//            em.joinTransaction(); //tells the EM to join the JTA UTx we are managing
            em.getTransaction().begin();
            dao.populate();
//            tx.commit();
            em.getTransaction().commit();
            logger.debug("populate complete");
        } catch (Exception ex) {
            try {
//                tx.rollback();
                em.getTransaction().rollback();
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
