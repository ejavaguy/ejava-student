package myorg.relex.one2one;

import javax.persistence.*;

/**
 * This class is an example of the inverse/parent side of a one-to-one, 
 * bi-directional relationship that allows 0..1 and changing related entities.
 */
@Entity(name="RelationAuto2")
@Table(name="RELATIONEX_AUTO2")
public class Auto2 {
	public enum Type { CAR, TRUCK };
	
	@Id @GeneratedValue
	private int id;
	@Enumerated(EnumType.STRING)
	@Column(length=10)
	private Type type;
	
	@OneToOne(
			optional=true, fetch=FetchType.LAZY)
	private Driver2 driver;
	
	public Auto2() {}
	public int getId() { return id;}

	public Type getType() { return type; }
	public void setType(Type type) {
		this.type = type;
	}

	public Driver2 getDriver() { return driver; }
	public void setDriver(Driver2 driver) {
		this.driver = driver;
	}
}
