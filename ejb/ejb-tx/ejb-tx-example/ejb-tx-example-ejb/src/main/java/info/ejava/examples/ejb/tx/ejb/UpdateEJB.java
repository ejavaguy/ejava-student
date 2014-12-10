package info.ejava.examples.ejb.tx.ejb;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class UpdateEJB {
    private static final Logger logger = LoggerFactory.getLogger(UpdateEJB.class);
    
    @PersistenceContext(unitName="ejbtx-warehouse")
    private EntityManager em;
    
    @EJB
    TxWatcherEJB txWatcher;

    @PostConstruct
    public void init() {
        logger.debug("*** {}:init({}) ***", getClass().getSimpleName(), super.hashCode());
    }
    @PreDestroy
    public void destroy() {
        logger.debug("*** {}:destroy({}) ***", getClass().getSimpleName(), super.hashCode());
    }
    
    
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void addQuantity(int productId, int quantity) {
        txWatcher.watchTransaction(getClass(), super.hashCode());
        logger.debug("adding {} to productId {}", quantity, productId);
        TypedQuery<Integer> getQuantity = em.createNamedQuery("EJBTxProduct.getQuantity",
                Integer.class)
                .setParameter("productId", productId);
        //get current value prior to attempting update
        List<Integer> values = getQuantity.getResultList();
        Integer currentValue = values.isEmpty() ? null : values.get(0);

        //perform the update
        int updated = em.createNamedQuery("EJBTxProduct.addQuantity")
                .setParameter("productId", productId)
                .setParameter("quantity", quantity)
                .executeUpdate();
        
        //get new value after making update
        values = getQuantity.getResultList();
        Integer newValue = values.isEmpty() ? null : values.get(0);
        
        logger.debug("updated {} rows for productId {}, old={}, new={}", updated, productId, currentValue, newValue);
    }
}
