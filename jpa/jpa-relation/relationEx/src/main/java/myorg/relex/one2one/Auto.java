package myorg.relex.one2one;

import javax.persistence.*;

/**
 * This class is an example of the inverse/parent side of a one-to-one, 
 * bi-directional relationship that allows 0..1 and changing related entities.
 */
@Entity(name="RelationAuto")
@Table(name="RELATIONEX_AUTO")
public class Auto {
	public enum Type { CAR, TRUCK };
	
	@Id @GeneratedValue
	private int id;
	@Enumerated(EnumType.STRING)
	@Column(length=10)
	private Type type;
	
	@OneToOne(
			mappedBy="auto", 
			optional=true, fetch=FetchType.LAZY)
	private Driver driver;
	
	public Auto() {}
	public int getId() { return id;}

	public Type getType() { return type; }
	public void setType(Type type) {
		this.type = type;
	}

	public Driver getDriver() { return driver; }
	public void setDriver(Driver driver) {
		this.driver = driver;
	}
}
