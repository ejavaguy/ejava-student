package myorg.relex.one2one;

import javax.persistence.*;

/**
 * This class provides an example of the owning/dependent side of a one-to-one
 * relationship where the inverse/parent represents a 0..1 or changing relation.
 */
@Entity
@Table(name="RELATIONEX_DRIVER")
public class Driver {
	@Id @GeneratedValue
	private int id;
	@Column(length=20)
	private String name;
	
	@OneToOne(
			optional=false,    //we must have the auto for this driver
			fetch=FetchType.EAGER)
	private Auto auto;
	
	protected Driver() {}
	public Driver(Auto auto) {
		this.auto = auto;
	}
	
	public int getId() { return id; }

	public Auto getAuto() { return auto; }
	public void setAuto(Auto auto) { //drivers can switch Autos
		this.auto = auto;
	}

	public String getName() { return name; }
	public void setName(String name) {
		this.name = name;
	}
}
