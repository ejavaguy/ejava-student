package myorg.relex.many2many;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
/**
 * This class provides an example of the owning side of a many-to-many, uni-directional relationship.
 */
@Entity
@Table(name="RELATIONEX_GROUP")
public class Group {
	@Id @GeneratedValue
	private int id;
	
	@ManyToMany
	@JoinTable(name="RELATIONEX_GROUP_MEMBER", 
			joinColumns=@JoinColumn(name="GROUP_ID"),
			inverseJoinColumns=@JoinColumn(name="MEMBER_ID"))
	Set<Individual> members;
	
	@Column(length=32, nullable=false)
	private String name;
	
	protected Group() {}
	public Group(String name) {
		this.name = name;
	}
	
	public int getId() { return id; }
	public Set<Individual> getMembers() {
		if (members == null) {
			members = new HashSet<Individual>();
		}
		return members;
	}
	
	public String getName() { return name; }
	public void setName(String name) {
		this.name = name;
	}
}
