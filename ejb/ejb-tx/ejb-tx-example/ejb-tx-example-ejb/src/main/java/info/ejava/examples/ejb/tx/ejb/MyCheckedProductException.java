package info.ejava.examples.ejb.tx.ejb;

import javax.ejb.ApplicationException;

import info.ejava.examples.ejb.tx.bo.Product;

@SuppressWarnings("serial")
@ApplicationException(rollback=false) //this is the default value for rollback
public class MyCheckedProductException extends Exception {
    private Product product;
    
    public MyCheckedProductException(Product product, String message) {
        super(message);
        this.product = product;
    }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) {
        this.product = product;
    }    
}
