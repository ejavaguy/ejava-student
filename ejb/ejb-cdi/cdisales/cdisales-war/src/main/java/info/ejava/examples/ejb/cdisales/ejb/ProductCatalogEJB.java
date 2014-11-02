package info.ejava.examples.ejb.cdisales.ejb;


import java.util.List;

import info.ejava.examples.ejb.cdisales.bl.Tx;
import info.ejava.examples.ejb.cdisales.bo.Member;
import info.ejava.examples.ejb.cdisales.bo.Product;
import info.ejava.examples.ejb.cdisales.dao.ProductCatalogDAO;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This EJB provides JTA transactional behavior to the DAO
 */
@Stateless
@Tx
public class ProductCatalogEJB implements ProductCatalogLocal {
    private static final Logger logger = LoggerFactory.getLogger(ProductCatalogEJB.class);

    //we are injecting the DAO type to avoid ambiguity with interface 
    @Inject
    ProductCatalogDAO dao;
    
    @PostConstruct
    public void init() {
        logger.debug("*** ProductCatalogEJB({}):init ***", super.hashCode());
    }
    
    @PreDestroy
    public void destroy() {
        logger.debug("*** ProductCatalogEJB({}):destroy ***", super.hashCode());
    }
    
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Product addProduct(Product product) {
        return dao.addProduct(product);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<Product> getProductsForSale(Product template, int offset, int limit) {
        return dao.getProductsForSale(template, offset, limit);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<Product> getBuyerProducts(Member buyer, int offset, int limit) {
        return dao.getBuyerProducts(buyer, offset, limit);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<Product> getSellerProducts(Member seller, int offset, int limit) {
        return dao.getSellerProducts(seller, offset, limit);
    }
    
    @Override
    public int remove(Product product) {
        return dao.remove(product);
    }
}
