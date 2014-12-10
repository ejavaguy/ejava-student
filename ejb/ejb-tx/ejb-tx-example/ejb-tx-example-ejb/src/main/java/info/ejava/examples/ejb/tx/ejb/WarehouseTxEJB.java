package info.ejava.examples.ejb.tx.ejb;

import info.ejava.examples.ejb.tx.bo.BeanCount;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class WarehouseTxEJB {
    private static final Logger logger = LoggerFactory.getLogger(WarehouseTxEJB.class);
    
    @PersistenceContext(unitName="ejbtx-warehouse")
    private EntityManager em;
    
    @EJB
    private TxWatcherEJB txWatcher;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @PostConstruct
    public void init() {
        logger.debug("*** {}:init({}) ***", getClass().getSimpleName(), super.hashCode());
        txWatcher.watchTransaction(getClass(), super.hashCode());
        updateBeanCount(1);
    }
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @PreDestroy
    public void destroy() {
        logger.debug("*** {}:destroy({}) ***", getClass().getSimpleName(), super.hashCode());
        txWatcher.watchTransaction(getClass(), super.hashCode());
        updateBeanCount(-1);
    }
    
    /**
     * This method will remove all data from all tables associated with the example
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int cleanup() {
        String entities[] = new String[]{ "Product", "Shipment"};
        int count=0;
        for (String entity : entities) {
            count+=em.createQuery(String.format("delete from %s", entity)).executeUpdate();
        }
        return count;
    }
    
    private void updateBeanCount(int value) {
        String beanName = getClass().getSimpleName();
        BeanCount count=em.find(BeanCount.class, 
                beanName, 
                LockModeType.PESSIMISTIC_WRITE);
        if (count!=null) {
            count.setCount(count.getCount()+value);
        } else {
            count = new BeanCount(beanName);
            count.setCount(value);
            em.persist(count);
        }
        logger.debug("updatedBeanCount({}) to {}", count.getName(), count.getCount());
    }
}
