package info.ejava.examples.ejb.tx.bo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name="EJBTX_PRODUCT") 
@NamedQueries({
    @NamedQuery(name="EJBTxProduct.addQuantity", 
        query="update Product p "
                + "set p.quantity=p.quantity + :quantity "
                + "where p.id=:productId"),
    @NamedQuery(name="EJBTxProduct.getQuantity", 
        query="select p.quantity from Product p "
                + "where p.id = :productId"),                
    @NamedQuery(name="EJBTxProduct.getRemainingQuantity", 
        query="select p.quantity - count(s) from Product p, Shipment s "
                + "where p.id = :productId and s.productId = :productId"),
    @NamedQuery(name="EJBTxProduct.getCount", 
        query="select count(p) from Product p "
                + "where p.id = :productId")
})
@NamedNativeQueries({
    @NamedNativeQuery(name="EJBTxProduct.getProduct", 
        query="select PRODUCT_ID, PRODUCT_NAME, QUANTITY from EJBTX_PRODUCT where PRODUCT_ID=:productId")
})
public class Product implements Serializable {
    @Id @GeneratedValue
    @Column(name="PRODUCT_ID")
    private int id;

    @Column(name="PRODUCT_NAME", length=16, nullable=false)
    private String name;
    
    @Column(name="QUANTITY")
    private int quantity;
    
    public Product() {}
    public Product(int id) { this.id = id;}
    public Product(String name, int quantity) {
        this.name=name;
        this.quantity=quantity;
    }
    public Product(Product p) {
        this.id = p.id;
        this.name = p.name;
        this.quantity = p.quantity;
    }

    public int getId() { return id; }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Product [id=").append(id)
            .append(", name=").append(name)
            .append(", quanity=").append(quantity).append("]");
        return builder.toString();
    }
}
