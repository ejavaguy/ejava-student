package ejava.examples.orm.rel.annotated;

import java.util.Date;

import javax.persistence.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Entity @Table(name="ORMREL_CHECKOUT")
public class Checkout {
    private static Log log = LogFactory.getLog(Checkout.class); 
    private static long CHECKOUT_DAYS = 1000 * 60 * 60 * 24 * 14;

    @Id @GeneratedValue @Column(name="CHECKOUT_ID")
    private long id;

    @Temporal(value=TemporalType.DATE)
    private Date outDate;

    @Temporal(value=TemporalType.DATE)
    private Date returnDate;
    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="CHECKOUT_BID")
    private Borrower borrower;
    
    protected Checkout() {  //this is needed by persistence layer
        log.debug(super.toString() + ": ctor()");
    }  
    public Checkout(Date outDate) {
        log.debug(super.toString() + ": ctor() outDate=" + outDate);
        this.outDate = outDate;
    }
    public boolean isOverdue() {
        return (returnDate == null) ?
            System.currentTimeMillis() - outDate.getTime() > CHECKOUT_DAYS : 
            returnDate.getTime() - outDate.getTime()  > CHECKOUT_DAYS;
    }
    
    public long getId() { return id; }

    public Borrower getBorrower() { return borrower; }
    public void setBorrower(Borrower borrower) {
        this.borrower = borrower;
    }

    public Date getReturnDate() { return returnDate; }
    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }
    
    public String toString() {
        return super.toString() +
            ", id=" + id +
            ", outDate=" + outDate +
            ", returnDate=" + returnDate +
            ", isOverdue=" + isOverdue() +
            ", borrower=" + borrower;
    }
}
