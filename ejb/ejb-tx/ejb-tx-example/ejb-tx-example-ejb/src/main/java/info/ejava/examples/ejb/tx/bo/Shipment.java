package info.ejava.examples.ejb.tx.bo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name="EJBTx_SHIPMENT")
@NamedQueries({
    @NamedQuery(name="EJBTxShipment.getCount", 
        query="select count(s) from Shipment s where s.productId = :productId")
})
public class Shipment implements Serializable {
    @Id @GeneratedValue
    @Column(name="SHIPMENT_ID")
    private int id;
    
    @Column(name="PRODUCT_ID", nullable=false, updatable=false)
    private int productId;
    
    @Column(name="LOCATION", length=16, nullable=false, updatable=false)
    private String location;
    
    protected Shipment() {}
    public Shipment(String location) {
        this.location = location;
    }
    
    public int getId() { return id; }    
    public int getProductId() { return productId; }
    public void setProductId(int productId) {
        this.productId = productId;
    }
    public String getLocation() { return location; }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Shipment [id=").append(id)
            .append(", productId=").append(productId)
            .append(", location=").append(location)
            .append("]");
        return builder.toString();
    }
}
