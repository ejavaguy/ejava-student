package ejava.examples.orm.ejbql.annotated;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name="ORMQL_SALE")
public class Sale {
    private long id;
    private Date date;
    private BigDecimal amount = new BigDecimal(0);
    private long buyerId;
    private Store store;
    private Collection<Clerk> clerks = new ArrayList<Clerk>();
    
    @Id @GeneratedValue @Column(name="SALE_ID")
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="ORMQL_SALE_CLERK_LINK",
            joinColumns={@JoinColumn(name="SALE_ID")},
            inverseJoinColumns={@JoinColumn(name="CLERK_ID")}
            )
    public Collection<Clerk> getClerks() {
        return clerks;
    }
    public void setClerks(Collection<Clerk> clerks) {
        this.clerks = clerks;
    }
    @ManyToOne(optional=false)
    @JoinColumn(name="SALE_STORE")
    public Store getStore() {
        return store;
    }
    public void setStore(Store store) {
        this.store = store;
    }    

    @Column(precision=5, scale=2)
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    @Column(nullable=false)
    public long getBuyerId() {
        return buyerId;
    }
    public void setBuyerId(long buyerId) {
        this.buyerId = buyerId;
    }
    
    @Temporal(TemporalType.DATE)
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("id=" + id);
        text.append(", date=" + date);
        text.append(", amount=" + amount);
        text.append(", buyer=" + buyerId);
        text.append(", clerks(" + clerks.size() + ")={");
        for(Clerk c : clerks) {
            text.append(c.getId() + ", ");
        }            
        text.append("}");
        return text.toString();
    }
}
