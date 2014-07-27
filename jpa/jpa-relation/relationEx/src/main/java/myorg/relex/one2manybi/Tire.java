package myorg.relex.one2manybi;

import javax.persistence.*;

/**
 * This class provides an example of the many/owning side of a many-to-one, bi-directional 
 * relationship mapped using a foreign key and that foreign key is used to derive the 
 * primary key of this class.
 */
@Entity
@Table(name="RELATIONEX_TIRE")
@IdClass(TirePK.class)
public class Tire {
	@Id
	@ManyToOne
	@JoinColumn(name="CAR_ID", nullable=false)
	private Car car;
	
	@Id @Enumerated(EnumType.STRING)
	@Column(length=16)
	private TirePosition position;
	
	private int miles;
	
	protected Tire() {}
	public Tire(Car car, TirePosition position) {
		this.car = car;
		this.position = position;
	}

	public TirePosition getPosition() { return position; }
	public Car getCar() { return car; }
	
	public int getMiles() { return miles; }
	public void setMiles(int miles) {
		this.miles = miles;
	}
	
	@Override
	public int hashCode() {
		return position.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		try {
			if (this==obj) { return true; }
			Tire rhs = (Tire)obj;
			return car.equals(rhs.car) && position==rhs.position; 
		} catch (Exception ex) { return false; }
	}
}
