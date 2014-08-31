package ejava.examples.asyncmarket.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.*;

@Entity @Table(name="ASYNCMARKET_BIDDER")
@NamedQueries({
    @NamedQuery(name="AsyncMarket_getAllPeople", 
            query="select p from Person p"),
    @NamedQuery(name="AsyncMarket_getPersonByUserId", 
                query="select p from Person p " +
                        "where p.userId=:userId")
})
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private long version;
    private String userId;
    private String name;
    private Collection<Bid> bids = new ArrayList<Bid>();
    private Collection<AuctionItem> items = new ArrayList<AuctionItem>();
    
    public Person() {}
    public Person(long id) { setId(id); }

    @Id @GeneratedValue
    public long getId() {
        return id;
    }
    private void setId(long id) {
        this.id = id;
    }
    @Version
    public long getVersion() {
        return version;
    }
    public void setVersion(long version) {
        this.version = version;
    }

    @Column(nullable=false, unique=true, updatable=false)
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy="bidder")
    public Collection<Bid> getBids() {
        return bids;
    }
    public void setBids(Collection<Bid> bids) {
        this.bids = bids;
    }
    
    @OneToMany(mappedBy="owner")
    public Collection<AuctionItem> getItems() {
        return items;
    }
    public void setItems(Collection<AuctionItem> items) {
        this.items = items;
    }
    
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("id=" + id);
        text.append(", version=" + version);
        text.append(", userId=" + userId);
        text.append(", name=" + name);
        text.append(", bids(" + bids.size() + ")");
        text.append(", items(" + items.size() + ")");
        return text.toString();
    }
}
