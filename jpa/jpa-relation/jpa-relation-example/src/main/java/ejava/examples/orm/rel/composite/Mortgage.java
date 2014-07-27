package ejava.examples.orm.rel.composite;

import javax.persistence.*;

/**
 * This class provides an example use of a composite @EmbeddedId primary key
 * where one of the properties of the Embeddable is derived from foreign key.
 */
@Entity @Table(name="ORMREL_MORTGAGE")
public class Mortgage {
	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name="mortgageId", column=@Column(name="MORTGAGE_ID"))
	})
	private MortgagePK pk;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="HOUSE_ID", nullable=false)
	@MapsId("houseId")
	private House house;
	
	public Mortgage() {}
	public Mortgage(House house, int mortgageId) {
		pk=new MortgagePK(house.getId(), mortgageId);
		this.house=house;
	}
	
	public MortgagePK getPk() { return pk; }
	public House getHouse() { return house; }
}
