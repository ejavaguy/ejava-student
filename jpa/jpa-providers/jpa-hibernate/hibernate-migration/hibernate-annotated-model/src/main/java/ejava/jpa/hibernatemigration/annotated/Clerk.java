package ejava.jpa.hibernatemigration.annotated;

import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * This class provides an example entity class that uses annotations to map to the database.
 * It inherits from a base class and contains a few dates. It is also the inverse side of
 * a Many-to-Many, bi-directional relationship.  
 */
@Entity
@Table(name="HMIG_CLERK")
public class Clerk extends Person {
	@ManyToMany(mappedBy="salesClerks")
	private Set<Sale> sales;
	@Basic(optional=false)
	@Temporal(TemporalType.DATE)
	@Column(name="HIRE_DATE")
	private Date hireDate;
	@Temporal(TemporalType.DATE)
	@Column(name="TERM_DATE")
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
