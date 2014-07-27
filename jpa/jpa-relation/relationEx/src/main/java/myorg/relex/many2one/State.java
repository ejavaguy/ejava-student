package myorg.relex.many2one;

import javax.persistence.*;

/**
 * This class provides an example one/parent entity in a many-to-one, uni-directional relationship.
 * For that reason -- this class will not have any reference to the many entities that may possibly 
 * reference it. These many/child objects must be obtained through the entity manager using a find or query.
 */
@Entity
@Table(name="RELATIONEX_STATE")
public class State {
	@Id @Column(length=2)
	private String id;
	
	@Column(length=20, nullable=false)
	private String name;
	
	protected State() {}
	public State(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public String getId() { return id; }

	public String getName() { return name; }
	public void setName(String name) {
		this.name = name;
	}
}
