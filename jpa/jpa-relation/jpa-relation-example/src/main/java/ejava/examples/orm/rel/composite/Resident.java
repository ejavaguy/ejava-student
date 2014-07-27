package ejava.examples.orm.rel.composite;

import javax.persistence.*;

/**
 * This class provides an example use of a composite @IdClass primary key
 * where one of the properties of the IdClass is derived from the foreign key.
 */
@Entity @Table(name="ORMREL_RESIDENT")
@IdClass(ResidentPK.class)
@AttributeOverrides({
	@AttributeOverride(name = "residentId", column=@Column(name="RESIDENT_ID"))
})
public class Resident {
	@Id 
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="HOUSE_ID", nullable=false)
	private House house;
	
	@Id
	private int residentId;	
	
	public Resident() {}
	public Resident(House house, int residentId) {
		this.house=house;
		this.residentId=residentId;
	}
	
	public House getHouse() { return house; }
	public int getResidentId() { return residentId; }	
}
