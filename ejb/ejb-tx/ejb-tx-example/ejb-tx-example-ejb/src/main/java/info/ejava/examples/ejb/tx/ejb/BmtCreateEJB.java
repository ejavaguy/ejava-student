package info.ejava.examples.ejb.tx.ejb;

import info.ejava.examples.ejb.tx.bo.Product;
import info.ejava.examples.ejb.tx.bo.Shipment;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.SynchronizationType;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class BmtCreateEJB {
    private static final Logger logger = LoggerFactory.getLogger(CreateEJB.class);
    
        //synchronization=SynchronizationType.SYNCHRONIZED is the default
    @PersistenceContext(unitName="ejbtx-warehouse", synchronization=SynchronizationType.UNSYNCHRONIZED)
    private EntityManager em;
    
    @EJB
    private TxWatcherEJB txWatcher;
    
    @Resource
    private UserTransaction utx;

    @PostConstruct
    public void init() {
        logger.debug("*** {}:init({}) ***", getClass().getSimpleName(), super.hashCode());
    }
    @PreDestroy
    public void destroy() {
        logger.debug("*** {}:destroy({}) ***", getClass().getSimpleName(), super.hashCode());
    }
    
    public Product createProduct(Product product) {
        try {
            txWatcher.watchTransaction(getClass(), super.hashCode());
            utx.begin();
            em.joinTransaction();
            em.persist(product);
            utx.commit();
            logger.debug("createProduct()={}", product);
            return product;
        } catch (Exception ex) {
            try { utx.rollback(); } 
            catch (Exception ex2) { throw new EJBException("error rolling back transaction", ex); }
            throw new EJBException("error managing transaction", ex);
        }
    }
    
    public Shipment createShipment(Shipment shipment) {
        txWatcher.watchTransaction(getClass(), super.hashCode());
        em.persist(shipment);
        logger.debug("createShipment()={}", shipment);
        return shipment;
    }
    
    public Shipment createShipment_RequiresNew(Shipment shipment) {
        txWatcher.watchTransaction(getClass(), super.hashCode());
        return createShipment(shipment);
    }
    
    public void flush() {
        logger.debug("calling flush");
        em.flush();
        logger.debug("flush complete");
    }
}
