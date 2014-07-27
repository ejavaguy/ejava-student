package ejava.examples.orm.rel.composite;

import javax.persistence.*;

/**
 * This class provides an example use of a composite @EmbeddedId primary key
 * where one of the properties of the Embeddable is re-used as a foreign key.
 */
@Entity @Table(name="ORMREL_DOOR")
public class Door {
	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name="houseId", column=@Column(name="HOUSE_ID")),
		@AttributeOverride(name="doorId", column=@Column(name="DOOR_ID"))
	})
	private DoorPK pk;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	//assign join column to primary key value and turn off inserts/updates here
	@JoinColumn(name="HOUSE_ID", insertable=false, updatable=false)
	private House house;
	
	public Door() {}
	public Door(House house, int doorId) {
		pk=new DoorPK(house.getId(), doorId);
		this.house=house;
	}
	
	public DoorPK getPk() { return pk; }
	public House getHouse() { return house; }
}
