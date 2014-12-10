package info.ejava.examples.ejb.tx.ejb;

import info.ejava.examples.ejb.tx.bo.Product;
import info.ejava.examples.ejb.tx.bo.Shipment;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class CreateEJB {
    private static final Logger logger = LoggerFactory.getLogger(CreateEJB.class);
    
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
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Product createProduct(Product product) {
        txWatcher.watchTransaction(getClass(), super.hashCode());
        em.persist(product);
        logger.debug("createProduct()={}", product);
        return product;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED) 
    public Shipment createShipment(Shipment shipment) {
        txWatcher.watchTransaction(getClass(), super.hashCode());
        em.persist(shipment);
        logger.debug("createShipment()={}", shipment);
        return shipment;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW) 
    public Shipment createShipment_RequiresNew(Shipment shipment) {
        txWatcher.watchTransaction(getClass(), super.hashCode());
        return createShipment(shipment);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void flush() {
        logger.debug("calling flush");
        em.flush();
        logger.debug("flush complete");
    }
}
