package ejava.examples.asyncmarket.bo;

import java.io.Serializable;

import javax.persistence.*;

@Entity @Table(name="ASYNCMARKET_BID")
public class Bid implements Serializable{
    private static final long serialVersionUID = 1L;
    private long id;
    private double amount;
    private Person person;
    private AuctionItem item;
    
    public Bid() {}
    public Bid(long id) {setId(id); }
    
    @Id @GeneratedValue
    public long getId() {
        return id;
    }
    private void setId(long id) {
        this.id = id;
    }
    
    @Column(scale=7, precision=2)
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    @ManyToOne
    public Person getBidder() {
        return person;
    }
    public void setBidder(Person person) {
        this.person = person;
    }
    
    @ManyToOne
    public AuctionItem getItem() {
        return item;
    }
    public void setItem(AuctionItem item) {
        this.item = item;
    }
    
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("id=" + id);
        text.append(", bidder=" + (person==null?null:person.getUserId()));
        text.append(", item=" + (item==null ? null : item.getId()));
        text.append(", $" + amount);
        return text.toString();
    }
}
