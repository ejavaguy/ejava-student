package ejava.examples.asyncmarket.bo;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import javax.persistence.*;

@NamedQueries({
    @NamedQuery(name="AsyncMarket_getAuctionItems",
            query="select ai from AuctionItem ai"),
    @NamedQuery(name="AsyncMarket_getAvailableAuctionItems",
            query="select ai from AuctionItem ai " +
                    "where ai.closed = false")
})
@Entity @Table(name="ASYNCMARKET_AUCTIONITEM")
public class AuctionItem implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    
    @Version
    private long version;
    
    @Column(length=32)
    private String name;
    @Column(length=32)
    private String productId;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    
    private double minBid;
    
    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    private Person owner;
    
    @OneToMany(mappedBy="item",
               fetch=FetchType.LAZY,
               cascade= {CascadeType.PERSIST,
                         CascadeType.REMOVE})
    private List<Bid> bids = new ArrayList<Bid>();
    private boolean closed=false;
    
    @OneToOne(fetch=FetchType.EAGER)
    private Bid winningBid;
    
    public AuctionItem() {}
    public AuctionItem(long id) { setId(id); }
    
    public long getId() {
        return id;
    }
    private void setId(long id) {
        this.id = id;
    }
    
    public long getVersion() {
        return version;
    }
    public void setVersion(long version) {
        this.version = version;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public String getProductId() {
        return productId;
    }
    public void setProductId(String productId) {
        this.productId = productId;
    }

    public double getMinBid() {
        return minBid;
    }
    public void setMinBid(double minBid) {
        this.minBid = minBid;
    }
    
    public Date getEndDate() {
        return endDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public Date getStartDate() {
        return startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Person getOwner() {
        return owner;
    }
    public void setOwner(Person owner) {
        this.owner = owner;
    }
    
    public void closeBids() {
        closed=true;
        setWinningBid(getHighestBid());        
    }
    
    public void setClosed(boolean closed) {
        this.closed = closed;
    }
    public boolean isClosed() {
        return closed;
    }
    
    public List<Bid> getBids() {
        return bids;
    }
    public void setBids(List<Bid> bids) {        
        this.bids = bids;
    }
    
    public void addBid(Bid bid) throws IllegalArgumentException {
        Bid highest = getHighestBid();
        if (highest == null || bid.getAmount() > highest.getAmount()) {
            bids.add(bid);
        }
    }
    
    @Transient
    public Bid getHighestBid() {
        Bid highest = null;
        for (Bid b : bids) {
            if (highest == null || b.getAmount() > highest.getAmount()) {
                highest = b;
            }
        }
        return highest;
    }
    
    public Bid getWinningBid() {
        return winningBid;
    }
    public void setWinningBid(Bid winningBid) {
        this.winningBid = winningBid;
    }
    
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("id=" + id);
        text.append(", name=" + name);
        text.append(", version=" + version);
        text.append(", startDate=" + startDate);
        text.append(", endDate=" + endDate);
        text.append(", midBid=$" + minBid);
        text.append(", bids(" + bids.size() + ")={");
        for(Bid b : bids) {
            text.append(b.getBidder() + " $" + b.getAmount() + ",");
        }
        text.append("}");
        text.append(", closed=" + closed);
        text.append(", winning bid=" + (winningBid==null ? null :
            winningBid.getAmount() + " $" + winningBid.getAmount()));
        return text.toString();
    }
}
