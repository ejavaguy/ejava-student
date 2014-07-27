package myorg.relex.one2one;

import javax.persistence.*;

/**
 * This class provides an example of the owning/dependent side of a one-to-one
 * relationship where the inverse/parent represents a 0..1 or changing relation.
 */
@Entity
@Table(name="RELATIONEX_DRIVER2")
public class Driver2 {
	@Id @GeneratedValue
	private int id;
	@Column(length=20)
	private String name;
	
	@OneToOne(mappedBy="driver",//driver is now the inverse side
			optional=false,    //we must have the auto for this driver
			fetch=FetchType.EAGER)
	private Auto2 auto;
	
	protected Driver2() {}
	public Driver2(Auto2 auto) {
		this.auto = auto;
	}
	
	public int getId() { return id; }

	public Auto2 getAuto() { return auto; }
	public void setAuto(Auto2 auto) { //drivers can switch Autos
		this.auto = auto;
	}

	public String getName() { return name; }
	public void setName(String name) {
		this.name = name;
	}
}
