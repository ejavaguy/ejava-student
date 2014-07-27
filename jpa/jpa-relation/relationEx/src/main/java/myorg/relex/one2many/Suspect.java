package myorg.relex.one2many;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * This class provides an example of the owning side of a collection of base data types.
 * In this case we want a unique set of strings (aliases) mapped to this entity using
 * a separate dependent table and a foreign key relationship.
 */
@Entity
@Table(name="RELATIONEX_SUSPECT")
public class Suspect {
	@Id @GeneratedValue
	private int id;
	@Column(length=32)
	private String name;
		
	@ElementCollection
	@CollectionTable(
			name="RELATIONEX_SUSPECT_ALIASES",
			joinColumns=@JoinColumn(name="SUSPECT_ID"), 
			uniqueConstraints=@UniqueConstraint(columnNames={"SUSPECT_ID", "ALIAS"}))
	@Column(name="ALIAS", length=32)
	private Set<String> aliases;

	public int getId() { return id; }

	public String getName() { return name; }
	public void setName(String name) {
		this.name = name;
	}

	public Set<String> getAliases() {
		if (aliases==null) { aliases = new HashSet<String>(); }
		return aliases;
	}
	public void setAliases(Set<String> aliases) {
		this.aliases = aliases;
	}
}
