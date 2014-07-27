package ejava.jpa.hibernatemigration.legacyhbm;

import java.util.HashSet;
import java.util.Set;

/**
 * This class provides an example legacy class that will use an external entity mapping. It 
 * extends a superclass and contains an enum. It is the inverse side of a OneToMany, 
 * bi-directional relationship.
 */
public class Customer extends Person {
	private Set<Sale> purchases;
	private String email;
	private CustomerLevel level=CustomerLevel.BRONZE;
	
	public Set<Sale> getPurchases() {
		if (purchases==null) { purchases=new HashSet<Sale>(); }
		return purchases; }
	public void setPurchaes(Sale sale) {
		getPurchases().add(sale);
	}
	
	public String getEmail() { return email; }
	public void setEmail(String email) {
		this.email = email;
	}
	
	public CustomerLevel getLevel() { return level; }
	public void setLevel(CustomerLevel level) {
		this.level = level;
	}

	@Override
	public int hashCode() {
		return super.hashCode() + (email==null?0:email.hashCode());
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			Customer rhs = (Customer)obj;
			return (super.equals(rhs)) && (email==null || email.equals(rhs.email));
		} catch (Exception ex) { return false; }
	}
	
	@Override
	public String toString() {
		return super.toString() +
				", email=" + email +
				", level=" + level +
				", purchases=" + getPurchases().size();
	}

}
