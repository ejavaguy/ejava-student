package myorg.relex.many2one;

import javax.persistence.*;

/**
 * This class provides an example of the owning side of a many-to-one, uni-directional relationship
 * that is realized through a foreign key from the child to the parent entity.
 */
@Entity
@Table(name="RELATIONEX_STATERES")
public class StateResident {
	@Id @GeneratedValue
	private int id;
	
	@ManyToOne(
			optional=false, 
			fetch=FetchType.EAGER
		)
	@JoinColumn(
			name="STATE_ID"//, 
//			nullable=false
		)
	private State state;
	
	@Column(length=32)
	private String name;
	
	protected StateResident() {}
	public StateResident(State state) {
		this.state = state;
	}

	public int getId() { return id; }

	public State getState() { return state; }
	public void setState(State state) {
		this.state = state;
	}
	
	public String getName() { return name; }
	public void setName(String name) {
		this.name = name;
	}
}
