package myorg.relex.many2one;

import java.io.Serializable;
import javax.persistence.*;

/**
 * This class provides an example compound primary key value that will be used in a many-to-one,
 * uni-directional relationship.
 */
@Embeddable
public class HousePK implements Serializable {
	private static final long serialVersionUID = 5213787609029123676L;
	@Column(name="NO")
	private int number;
	@Column(name="STR", length=50)
	private String street;
	
	public HousePK() {}
	public HousePK(int number, String street) {
		this.number = number;
		this.street = street;
	}
	
	public int getNumber() { return number; }
	public void setNumber(int number) {
		this.number = number;
	}
	
	public String getStreet() { return street; }
	public void setStreet(String street) {
		this.street = street;
	}

	@Override
	public int hashCode() {
		return number + street==null?0:street.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			if (this==obj) { return true; }
			HousePK rhs = (HousePK)obj;
			if (street==null && rhs.street != null) { return false; }
			return number==rhs.number && street.equals(rhs.street);
		} catch (Exception ex) { return false; }
	}	
}
