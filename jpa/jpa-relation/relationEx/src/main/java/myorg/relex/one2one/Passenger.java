package myorg.relex.one2one;

import javax.persistence.*;

/**
 * This entity class provides an example of the owning side of a 
 * bi-directional relation where all cascades are being initiated
 * from the inverse side (i.e., not from here).
 */
@Entity
@Table(name="RELATIONEX_PASSENGER")
public class Passenger {
    @Id @GeneratedValue
    private int id;
    
    @OneToOne(optional=false)
    private Ticket ticket;

    @Column(length=32, nullable=false)
    private String name;
    
    protected Passenger() {}
    public Passenger(int id) { this.id = id; }
	public Passenger(Ticket ticket, String name) {
		this.ticket = ticket;
		this.name = name;
	}
	
	public int getId() { return id; }

	public Ticket getTicket() { return ticket; }
	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}
	
	public String getName() { return name;}
	public void setName(String name) {
		this.name = name;
	}
}
