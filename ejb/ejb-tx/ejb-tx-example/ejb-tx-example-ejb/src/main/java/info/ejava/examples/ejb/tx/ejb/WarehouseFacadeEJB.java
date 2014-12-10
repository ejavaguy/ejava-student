package info.ejava.examples.ejb.tx.ejb;

import java.util.List;

import info.ejava.examples.ejb.tx.bo.Product;
import info.ejava.examples.ejb.tx.bo.Shipment;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class WarehouseFacadeEJB implements WarehouseRemote {
    private static final Logger logger = LoggerFactory.getLogger(WarehouseFacadeEJB.class);

    @Resource
    private SessionContext ctx;
    @EJB
    private WarehouseTxEJB txHelper;
    @EJB
    private TxWatcherEJB txWatcher;
    //these beans are given random names because we purposely spread behavior 
    //across multiple EJBs to show how the transaction context propagates to 
    //independent EJBs. Note none of them will share the same PersistenceContext
    //since this EJB does not declare one.
    @EJB
    private CreateEJB beanA;
    @EJB
    private UpdateEJB beanB;
    @EJB
    private GetEJB beanC;
    @EJB
    private BmtCreateEJB bmtA;
    
    @PostConstruct
    public void init() {
        logger.debug("*** {}:init({}) ***", getClass().getSimpleName(), super.hashCode());
    }
    @PreDestroy
    public void destroy() {
        logger.debug("*** {}:destroy({}) ***", getClass().getSimpleName(), super.hashCode());
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public int cleanup() {
        txWatcher.watchTransaction(getClass(), super.hashCode());
        return txHelper.cleanup();
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Product getProduct(int productId) {
        txWatcher.watchTransaction(getClass(), super.hashCode());
        return beanC.getProduct(productId);
    }
    
    /**
     * The method creates an overall transaction and enlists the work of three
     * separate EJBs to complete the work. No persistence context will be shared
     * between this and the three called EJBs. All interaction will at the 
     * database level.
     * @throws UnexpectedState 
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Product createProductAndShipments(Product product, List<Shipment> shipments) throws UnexpectedState {
        //enlist a stateful EJB to watch the transaction events
        txWatcher.watchTransaction(getClass(), super.hashCode());
        
        //create a product in the database
        Product p=beanA.createProduct(product);
        
        //check the count in the DB for the product
        int products = beanC.getProductCount(p.getId());
        if (products!=1) {
            throw new UnexpectedState("product", 1, products);
        }
        
        //add shipments to the database
        for (Shipment shipment: shipments) {
            shipment.setProductId(p.getId());
            beanA.createShipment(shipment);
        }

        //verify the shipments exist for the product
        int shipmentCount = beanC.getShipmentCount(p.getId());
        if (shipmentCount != shipments.size()) {
            throw new UnexpectedState("shipments", shipments.size(), shipmentCount);
        }
        
        //get the value from the database
        int remainingQty=beanC.getRemainingQuantity(p.getId());
        if (remainingQty >= 0) {
            product = new Product(product); //don't modify the managed instance
            product.setQuantity(remainingQty);
        }
        return product;
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Product createProductAndShipments_NotSupported(Product product, List<Shipment> shipments) throws UnexpectedState {
        return createProductAndShipments(product, shipments);
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Product createProductAndShipments_RequiresNew(Product product, List<Shipment> shipments) throws UnexpectedState {
        //enlist a stateful EJB to watch the transaction events
        txWatcher.watchTransaction(getClass(), super.hashCode());
        
        //create a product in the database
        Product p=beanA.createProduct(product);
        
        //check the count in the DB for the product
        //since this is in a new transaction -- it should not yet be able to see what we created
        int products = beanC.getProductCount_RequiresNew(p.getId());
        if (products!=0) {
            throw new UnexpectedState("product", 0, products);
        }
        
        //add shipments to the database
        for (Shipment shipment: shipments) {
            shipment.setProductId(p.getId());
            beanA.createShipment(shipment);
        }

        //verify the shipments exist for the product
        //since this is in a new transaction -- it should not yet be able to see what we created
        int shipmentCount = beanC.getShipmentCount_RequiresNew(p.getId());
        if (shipmentCount != 0) {
            throw new UnexpectedState("shipments", 0, shipmentCount);
        }
        
        //get the value from the database
        int remainingQty=beanC.getRemainingQuantity(p.getId());
        if (remainingQty >= 0) {
            product = new Product(product); //don't modify the managed instance
            product.setQuantity(remainingQty);
        }
        return product;
    }
    
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Product createProductAndShipments_Async(Product product, int quantity) throws UnexpectedState {
        //enlist a stateful EJB to watch the transaction events
        txWatcher.watchTransaction(getClass(), super.hashCode());
        
        //create a product in the database
        Product p=beanA.createProduct(product);
        
        
        //return an instance of the bean from this thread/transaction
        beanC.getProduct(p.getId());
        
        //update product quantity in separate thread
        beanB.addQuantity(p.getId(), quantity);
        return p;
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Product createProductAndThrowChecked(Product product) throws MyCheckedProductException {
        //enlist a stateful EJB to watch the transaction events
        txWatcher.watchTransaction(getClass(), super.hashCode());
        
        //create a product in the database
        Product p=beanA.createProduct(product);

        //throw a checked exception -- the product will still be committed
        logger.debug("thowing checked exception without rolling back transaction");
        throw new MyCheckedProductException(p, "planned checked exception");
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Product createProductAndThrowCheckedRollback(Product product) throws MyCheckedRollbackProductException  {
        //enlist a stateful EJB to watch the transaction events
        txWatcher.watchTransaction(getClass(), super.hashCode());
        
        //create a product in the database
        Product p=beanA.createProduct(product);
        beanA.flush();

        //throw a checked exception marked for rollback -- the product will *NOT* be committed
        logger.debug("thowing checked exception with rollback=true transaction");
        throw new MyCheckedRollbackProductException(p, "planned checked exception");
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Product createProductAndRollback(Product product) {
        //enlist a stateful EJB to watch the transaction events
        txWatcher.watchTransaction(getClass(), super.hashCode());
        
        //create a product in the database
        Product p=beanA.createProduct(product);
        beanA.flush();

        ctx.setRollbackOnly();
        return p; //return the product even thow we are going to roll it back
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Product createProductBmt(Product product) {
        //enlist a stateful EJB to watch the transaction events
        txWatcher.watchTransaction(getClass(), super.hashCode());
        
        //create a product in the database
        Product p=bmtA.createProduct(product);
        //bmtA.flush();

        //return an instance of the bean from this thread/transaction
        product=beanC.getProduct(p.getId());
        
        return product;
    }
}
