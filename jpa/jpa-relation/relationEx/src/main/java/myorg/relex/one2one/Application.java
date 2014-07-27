package myorg.relex.one2one;

import java.util.Date;

import javax.persistence.*;

/**
 * This class provides an example of the owning side
 * of a one-to-one, bi-directional relationship.
 */
@Entity
@Table(name="RELATIONEX_APPLICATION")
public class Application {
    @Id
    private int id;
    @MapsId //foreign key realizes primary key
    @OneToOne(//lack of mappedBy identifies this as owning side 
    		  optional=false, fetch=FetchType.EAGER)
    private Applicant applicant;
    
    @Temporal(TemporalType.DATE)
    private Date desiredStartDate;

    protected Application() {}
    public Application(Applicant applicant) {
    	this.applicant = applicant;
    	if (applicant != null) { 
    		applicant.setApplication(this); //must maintain inverse side 
    	}
	}
    
	public int getId() { return id; }
	public Applicant getApplicant() { return applicant; }

	public Date getDesiredStartDate() { return desiredStartDate; }
	public void setDesiredStartDate(Date desiredStartDate) {
		this.desiredStartDate = desiredStartDate;
	}
}
