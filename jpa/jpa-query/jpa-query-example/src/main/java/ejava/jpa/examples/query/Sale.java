package ejava.jpa.examples.query;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.*;

@Entity
@Table(name="JPAQL_SALE")
public class Sale {
    @Id @GeneratedValue 
    @Column(name="SALE_ID")
    private long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(precision=5, scale=2)
    private BigDecimal amount = new BigDecimal(0);
    
    /** This property has been purposely modeled as an ID and not a
     * relationship to show how JPA queries can still functionally associate
     * information without an explicit foreign key
     */
    @Column(name="BUYER_ID", nullable=false)
    private long buyerId;

    @ManyToOne(optional=false)
    @JoinColumn(name="SALE_STORE")
    private Store store;

    @ManyToMany
    @JoinTable(name="JPAQL_SALE_CLERK_LINK",
        joinColumns={@JoinColumn(name="SALE_ID")},
        inverseJoinColumns={@JoinColumn(name="CLERK_ID")}
        )
    private List<Clerk> clerks = new ArrayList<Clerk>();
    
    public long getId() { return id; }
    
    public List<Clerk> getClerks() { return clerks; }
    public Sale setClerks(List<Clerk> clerks) {
        this.clerks = clerks;
        return this;
    }
    public Sale addClerk(Clerk...clerk) {
    	if (clerk!=null) {
    		for (Clerk c : clerk) {
    			if (c != null) { clerks.add(c); }
    		}
    	}
    	return this;
    }
    
    public Store getStore() { return store; }
    public Sale setStore(Store store) {
        this.store = store;
        return this;
    }    

    public BigDecimal getAmount() { return amount; }
    public Sale setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }
    
    public long getBuyerId() { return buyerId; }
    public Sale setBuyerId(long buyerId) {
        this.buyerId = buyerId;
        return this;
    }
    
    public Date getDate() { return date; }
    public Sale setDate(Date date) {
        this.date = date;
        return this;
    }
    
    public String toString() {
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
        StringBuilder text = new StringBuilder();
        text.append("date=" + (date==null ? null : df.format(date)));
        text.append(", amount=" + nf.format(amount));
        text.append(", buyer=" + buyerId);
        text.append(", clerks(" + clerks.size() + ")={");
        for(Clerk c : clerks) {
            text.append(c.getId() + ", ");
        }            
        text.append("}");
        return text.toString();
    }
}
