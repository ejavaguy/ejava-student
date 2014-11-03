package info.ejava.examples.ejb.cdisales.ejb;

import info.ejava.examples.ejb.cdisales.bl.ProductCatalog;
import info.ejava.examples.ejb.cdisales.bl.Tx;

import javax.ejb.Local;

@Local
@Tx
public interface ProductCatalogLocal extends ProductCatalog {
    
}
