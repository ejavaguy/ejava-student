package myorg.relex.many2one;

import javax.persistence.*;

/**
 * This class provides an example of the owning/child side of a many-to-one, uni-directional relationship
 * where the parent uses a (embedded) compound primary key.
 */
@Entity
@Table(name="RELATIONEX_OCCUPANT")
public class Occupant {
	@Id @GeneratedValue
	private int id;
	
	@ManyToOne(optional=false)
	@JoinColumns({
			@JoinColumn(name="RES_NUM", referencedColumnName="NO"),
			@JoinColumn(name="RES_STR", referencedColumnName="STREET_NAME")
	})
	private House residence;
	
	@Column(length=16, nullable=false)
	private String name;

	protected Occupant(){}
	public Occupant(String name, House residence) {
		this.name = name;
		this.residence = residence;
	}
	
	public int getId() { return id; }

	public House getResidence() { return residence; }
	public void setResidence(House residence) {
		this.residence = residence;
	}

	public String getName() { return name; }
	public void setName(String name) {
		this.name = name;
	}
}
