package info.ejava.examples.ejb.tx.ejb;

import java.util.List;

import info.ejava.examples.ejb.tx.bo.Product;

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
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class GetEJB {
    private static final Logger logger = LoggerFactory.getLogger(GetEJB.class);
    
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
    
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public int getRemainingQuantity(int productId) {
        txWatcher.watchTransaction(getClass(), super.hashCode());
        Long remainingQty = em.createNamedQuery("EJBTxProduct.getRemainingQuantity", Long.class)
            .setParameter("productId", productId)
            .getSingleResult();
        logger.debug("getRemainingQuantity(productId={})={}", productId, remainingQty);
        return remainingQty==null ? -1 : remainingQty.intValue();
    }
    
    
    /**
     * Returns a product from the database in the existing or no transaction. No
     * transaction is required if none exists.
     */
    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Product getProduct(int productId) {
        txWatcher.watchTransaction(getClass(), super.hashCode());
        //hit the DB directly and get unmanaged data
        List<Object[]> result = em.createNamedQuery("EJBTxProduct.getProduct")
                .setParameter("productId", productId)
                .getResultList();
        
        Product product=null;
        if (!result.isEmpty()) {
            Object[] values = result.get(0);
            product=new Product((int) values[0]);
            product.setName((String) values[1]);
            product.setQuantity((int) values[2]);
        }
        
        logger.debug("getProduct({})={}", productId, product);
        return product;
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Product getProduct_NotSupported(int productId) {
        return getProduct(productId);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Product getProduct_RequiresNew(int productId) {
        return getProduct(productId);
    }
    
    @TransactionAttribute(TransactionAttributeType.SUPPORTS) 
    public int getProductCount(int productId) {
        txWatcher.watchTransaction(getClass(), super.hashCode());
        Long products = em.createNamedQuery("EJBTxProduct.getCount", Long.class)
                .setParameter("productId", productId)
                .getSingleResult();
        logger.debug("getProductCount(productId={})={}", productId, products);
        return products==null ? -1 : products.intValue();
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW) 
    public int getProductCount_RequiresNew(int productId) {
        return getProductCount(productId);
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED) 
    public int getProductCount_NotSupported(int productId) {
        return getProductCount(productId);
    }
    
    
    @TransactionAttribute(TransactionAttributeType.SUPPORTS) 
    public int getShipmentCount(int productId) {
        txWatcher.watchTransaction(getClass(), super.hashCode());
        Long shipments = em.createNamedQuery("EJBTxShipment.getCount", Long.class)
                .setParameter("productId", productId)
                .getSingleResult();
        logger.debug("getShipmentCount(productId={})={}", productId, shipments);
        return shipments==null ? -1 : shipments.intValue();
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW) 
    public int getShipmentCount_RequiresNew(int productId) {
        return getShipmentCount(productId);
    }
}
