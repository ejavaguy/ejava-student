package myorg.relex.one2one;

import javax.persistence.*;

/**
 * This entity class provides an example of a dependent with a relationship to a parent entity that
 * should only exist to support this entity. When this entity ceases to reference the parent, it 
 * will become "orphaned" and subject to orphanRemoval by the provider.
 */
@Entity
@Table(name="RELATIONEX_ATTENDEE")
public class Attendee {
    @Id @GeneratedValue
    private int id;
    
    //orphanRemoval will take care of dereference and DELETE from dependent Attendee 
    @OneToOne(cascade=CascadeType.PERSIST, orphanRemoval=true)
    private Residence residence;
    
    private String name;

	public int getId() { return id; }

	public Residence getResidence() { return residence; }
	public void setResidence(Residence residence) {
		this.residence = residence;
	}

	public String getName() { return name; }
	public void setName(String name) {
		this.name = name;
	}
}
