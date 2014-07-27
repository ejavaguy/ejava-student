package myorg.relex.one2manybi;

import java.util.Date;

import javax.persistence.*;
/**
 * This class provides an example of the many/child side of a many-to-one, bi-directional relationship.
 * Being the many side of the many-to-one relationship, this class must implementing the owning side.
 */
@Entity
@Table(name="RELATIONEX_LOAN")
public class Loan {
    @Id @GeneratedValue
    private int id;
    
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="BORROWER_ID")
    private Borrower borrower;
    
    @Temporal(TemporalType.DATE)
    @Column(nullable=false)
    private Date checkout;
    @Temporal(TemporalType.DATE)
    private Date checkin;
    
    public Loan() {}
    public Loan(Borrower borrower) {
    	this.borrower=borrower;
    	this.checkout=new Date();
    }
	
    public int getId() { return id; }
    public boolean isOut() { return checkin==null; }
	
	public Borrower getBorrower() { return borrower; }
	public void setBorrower(Borrower borrower) {
		this.borrower = borrower;
	}
	
	public Date getCheckout() { return checkout; }
	public void setCheckout(Date checkout) {
		this.checkout = checkout;
	}
	
	public Date getCheckin() { return checkin; }
	public void setCheckin(Date checkin) {
		this.checkin = checkin;
	}
}
