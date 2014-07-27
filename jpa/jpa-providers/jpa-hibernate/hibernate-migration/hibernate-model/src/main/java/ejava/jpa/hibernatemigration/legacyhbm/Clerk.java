package ejava.jpa.hibernatemigration.legacyhbm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * This class provides an example legacy entity class that will be mapped to the database.
 * It inherits from a base class and contains a few dates. It is also the inverse side of
 * a Many-to-Many, bi-directional relationship.  
 */
public class Clerk extends Person {
	private Set<Sale> sales;
	private Date hireDate;
	private Date termDate;
	
	public Clerk() {}
	public Clerk(int id) {
		super(id);
	}
	
	public Set<Sale> getSales() {
		if (sales==null) { sales = new HashSet<Sale>(); }
		return sales; 
	}
	public void addSale(Sale sale) {
		getSales().add(sale);
	}
	
	public Date getHireDate() { return hireDate; }
	public void setHireDate(Date hireDate) {
		this.hireDate = hireDate;
	}
	
	public Date getTermDate() { return termDate; }
	public void setTermDate(Date termDate) {
		this.termDate = termDate;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode() + (hireDate==null?0:hireDate.hashCode());
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			Clerk rhs = (Clerk)obj;
			return super.equals(rhs) && (hireDate==null || hireDate.equals(rhs.hireDate));
		} catch (Exception ex) { return false; }
	}
	
	@Override
	public String toString() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return super.toString() + 
				", hireDate=" + (hireDate==null?null:df.format(hireDate)) +
						", hireDate=" + (termDate==null?null:df.format(termDate)) +
						", sales=" + getSales().size();
	}
}
