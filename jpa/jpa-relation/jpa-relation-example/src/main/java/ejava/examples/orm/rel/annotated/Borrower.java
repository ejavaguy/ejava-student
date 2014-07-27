package ejava.examples.orm.rel.annotated;

import java.util.Date;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;

import javax.persistence.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class forms a uni-directional, one-to-one relationship with the
 * Person class, joined by common primary keys. There is not additional
 * column necessary to store the foreign key.
 * 
 * Note too that this object fully encapsulates the relationship by never
 * handing over the Person; only using it to derive values. All concenience
 * getters are declared Transient.
 */
@Entity @Table(name="ORMREL_BORROWER")
public class Borrower  {
    private static Log log = LogFactory.getLog(Borrower.class);
    @Id @Column(name="BORROWER_ID")
    private long id;
    @Temporal(value=TemporalType.DATE)
    private Date startDate;
    @Temporal(value=TemporalType.DATE)
    private Date endDate;
    
    @OneToOne(fetch=FetchType.LAZY, optional=false, 
            cascade={CascadeType.PERSIST, 
                     CascadeType.REFRESH,
                     CascadeType.MERGE})
    @PrimaryKeyJoinColumn  //the two tables will be joined by PKs
    //@MapsId
    private Person identity;
    
    @OneToOne(fetch=FetchType.LAZY, 
            optional=true,       //lets make this optional for demo 
            mappedBy="borrower") //the other side owns foreign key column
    private Applicant application;
    
    @OneToMany(mappedBy="borrower", //this relationship is owned by Checkout
            fetch=FetchType.LAZY)   //try to limit what we get back
    private Collection<Checkout> checkouts = new ArrayList<Checkout>();
    
    @SuppressWarnings("unused")
    private Borrower() { log.info(super.toString() + ", ctor()"); }
    public Borrower(Person identity) {
        log.info(super.toString() + ", ctor():" + identity);
        this.id = identity.getId();
        this.identity = identity;
    }

    public long getId() { return id; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date end) {
        this.endDate = end;
    }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date start) {
        this.startDate = start;
    }
    
    /** This method returns the concatenated first/last names of the 
     * Borrower's person. Note that we need to declare this field Transient
     * so that the provider doesn't try to map this to a column in the 
     * Borrower table as well as look for a setName.
     */
    //@Transient - not needed when using FIELD mapping versus PROPERTY mapping
    public String getName() {
        return identity.getFirstName() + " " + identity.getLastName();
    }

    public Applicant getApplication() { return application; }
    public void setApplication(Applicant application) {
        this.application = application;
    }

    public Collection<Checkout> getCheckouts() {
        //don't let them directly touch our collection
        return new ArrayList<Checkout>(checkouts);
    }
    public void addCheckout(Checkout checkout) {
        this.checkouts.add(checkout);
    }
    public void removeCheckout(Checkout checkout) {
        this.checkouts.remove(checkout);
    }
    
    public String toString() {
        StringBuilder text = new StringBuilder(
            getClass().getName() +
            ", id=" + id +
            ", startDate=" + startDate +
            ", endDate=" + endDate +
            ", identity=" + identity +
            ", applicant=" + application +
            ", checkouts={");
        for(Iterator<Checkout> itr=checkouts.iterator(); itr.hasNext();) {
            text.append(itr.next().getId());
            if (itr.hasNext()) { text.append(","); }
        }
        text.append("}");
        return text.toString();
    }
}
