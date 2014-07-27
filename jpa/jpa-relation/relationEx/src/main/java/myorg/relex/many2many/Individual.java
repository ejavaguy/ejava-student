package myorg.relex.many2many;

import javax.persistence.*;

/**
 * This class provides an example of the inverse side of a many-to-many, uni-directional relationship
 */
@Entity
@Table(name="RELATIONEX_INDIVIDUAL")
public class Individual {
	@Id @GeneratedValue
	private int id;
	
	@Column(length=32, nullable=false)
	private String name;
	
	protected Individual() {}
	public Individual(String name) {
		this.name = name;
	}

	public int getId() { return id; }

	public String getName() { return name; }
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public int hashCode() {
		return name==null? 0 : name.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			if (this == obj)  return true;
			Individual rhs = (Individual) obj;
			if (name==null && rhs.name != null) { return false; }
			return name.equals(rhs.name);
		} catch (Exception ex) { return false; }
	}
}
