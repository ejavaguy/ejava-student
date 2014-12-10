package info.ejava.examples.ejb.tx.ejb;

import javax.ejb.ApplicationException;

import info.ejava.examples.ejb.tx.bo.Product;

@SuppressWarnings("serial")
@ApplicationException(rollback=true)
public class MyCheckedRollbackProductException extends Exception {
    private Product product;
    
    public MyCheckedRollbackProductException(Product product, String message) {
        super(message);
        this.product = product;
    }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) {
        this.product = product;
    }    
}
