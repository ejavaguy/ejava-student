package ejava.examples.orm.rel.annotated;

import javax.persistence.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The class provides a set of OneToOne relationship examples.
 * It contains a uni-directional, non-primary key relationship to Person and a 
 * bi-directional, non-primary key relationship with Borrower. In both cases
 * the Applicant owns the relationship. This means that the foreign key forming
 * the relationships will exist in the Applicant's table.
 */
@Entity @Table(name="ORMREL_APPLICANT")
public class Applicant  {
    private static Log log = LogFactory.getLog(Applicant.class);
    @Id @GeneratedValue
    private long id;
    
    @OneToOne(optional=false)       //we must have a Person
    @JoinColumn(name="APP_PERSON")  //name of our foreign key column
    private Person identity;
    
    @OneToOne(optional=true)        //we may exist without Borrower 
    @JoinColumn(name="APP_BORROWER")//we own relationship to Borrower
    private Borrower borrower;
    
    public Applicant() {
        log.info(super.toString() + ", ctor()");     }
    
    public long getId() { return id; }

    public Borrower getBorrower() { return borrower; }
    public void setBorrower(Borrower borrower) {
        this.borrower = borrower;
    }

    public Person getIdentity() { return identity; }
    public void setIdentity(Person identity) {
        this.identity = identity;
    }

    public String toString() {
        return super.toString() +
            "id=" + id + 
            ", identity=" + ((identity==null) ? "null" : identity.getId()) +
            ", borrower=" + ((borrower==null) ? "null" : borrower.getId());
    }

}
