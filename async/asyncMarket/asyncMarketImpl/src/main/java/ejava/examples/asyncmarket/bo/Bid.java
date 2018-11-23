package ejava.examples.asyncmarket.bo;

import java.io.Serializable;

import javax.persistence.*;

@Entity @Table(name="ASYNCMARKET_BID")
public class Bid implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    
    @Column(scale=7, precision=2)
    private double amount;
    
    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(nullable=false, updatable=false)
    private Person bidder;
    
    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(nullable=false, updatable=false)
    private AuctionItem item;
    
    public Bid() {}
    public Bid(long id) {setId(id); }
    
    public long getId() {
        return id;
    }
    private void setId(long id) {
        this.id = id;
    }
    
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public Person getBidder() {
        return bidder;
    }
    public void setBidder(Person bidder) {
        this.bidder = bidder;
    }
    
    public AuctionItem getItem() {
        return item;
    }
    public void setItem(AuctionItem item) {
        this.item = item;
    }
    
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("id=" + id);
        text.append(", bidder=" + (bidder==null?null:bidder.getUserId()));
        text.append(", item=" + (item==null ? null : item.getId()));
        text.append(", $" + amount);
        return text.toString();
    }
}
