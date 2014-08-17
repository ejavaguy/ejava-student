package ejava.jpa.hibernatemigration.annotated;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * This class provides an example legacy class that will use an external entity mapping within a mix of other
 * classes that use annotations. It acts as the inverse side of a One-to-Many, bi-directional relationship and 
 * the owning side of a Many-to-Many relationship. It uses a manually generated Id, a date, and decimal value 
 * that should require some attention in the mapping.
 */
public class Sale {
	private String id;
	private BigDecimal amount;
	private Date dateTime;
	private Set<Clerk> salesClerks;
	private Customer customer;

	public Sale() {
		this(UUID.randomUUID().toString());
	}
	public Sale(String id) {
		this.id = id;
	}
	
	public String getId() { return id; }
	
	public Date getDateTime() { return dateTime; }
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
	
	public BigDecimal getAmount() { return amount; }
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public Set<Clerk> getSalesClerks() {
		if (salesClerks==null) { salesClerks=new HashSet<Clerk>(); }
		return salesClerks; 
	}
	public void addSalesClerk(Clerk clerk) {
		getSalesClerks().add(clerk);
	}
	
	public Customer getCustomer() { return customer; }
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	@Override
	public int hashCode() { return id.hashCode(); }
	@Override
	public boolean equals(Object obj) {
		try {
			Sale rhs = (Sale)obj;
			return id.equals(rhs.id);
		} catch (Exception ex) { return false; }
	}
	
	@Override
	public String toString() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
		return "id=" + id +
				", date=" + (dateTime==null?null:df.format(dateTime)) +
				", amount=" + (amount==null?null:NumberFormat.getCurrencyInstance().format(amount)) +
				",\ncustomer=" + customer +
				",\nclerks=" + salesClerks;
	}
}
