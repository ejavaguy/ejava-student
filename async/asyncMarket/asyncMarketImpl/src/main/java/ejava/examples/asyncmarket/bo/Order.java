package ejava.examples.asyncmarket.bo;

import java.io.Serializable;

import javax.persistence.*;

@Entity @Table(name="ASYNCMARKET_ORDER")
@NamedQueries({
    @NamedQuery(name="AsyncMarket_getOrders",
                query="select o from Order o"),
    @NamedQuery(name="AsyncMarket_getOrdersForItem",
                query="select o from Order o " +
                      "where o.item.id = :itemId")
})
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    
    @Version
    private long version;
    
    @ManyToOne(optional=false, fetch=FetchType.EAGER)
    private Person buyer;
    
    @ManyToOne(optional=false, fetch=FetchType.EAGER)
    private AuctionItem item;
    
    private double maxBid;
    
    public Order() {}
    public Order(long id) { setId(id); }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    
    public long getVersion() {
        return version;
    }
    public void setVersion(long version) {
        this.version = version;
    }

    public Person getBuyer() {
        return buyer;
    }
    public void setBuyer(Person buyer) {
        this.buyer = buyer;
    }
    
    public AuctionItem getItem() {
        return item;
    }
    public void setItem(AuctionItem item) {
        this.item = item;
    }
    
    public double getMaxBid() {
        return maxBid;
    }
    public void setMaxBid(double maxBid) {
        this.maxBid = maxBid;
    }
    
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("id=" + id);
        text.append(", version=" + version);
        text.append(", maxBid=" + maxBid);
        text.append(", buyer=" + buyer.getUserId());
        text.append(", item=" + item);
        text.append(", highestBid=" + item.getHighestBid());
        return text.toString();
    }
}