package myorg.relex.many2one;

import javax.persistence.*;

/**
 * This class provides an example of a parent/inverse side of a many-to-one, uni-directional relationship where
 * the parent and foreign key must use a compound value.
 */
@Entity
@Table(name="RELATIONEX_HOUSE")
public class House {
	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name="street", column=@Column(name="STREET_NAME", length=20)),
	})
	private HousePK id;
	
	@Column(length=16, nullable=false)
	private String name;
	
	protected House() {}
	public House(HousePK id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public HousePK getId() { return id; }

	public String getName() { return name; }
	public void setName(String name) {
		this.name = name;
	}
}
