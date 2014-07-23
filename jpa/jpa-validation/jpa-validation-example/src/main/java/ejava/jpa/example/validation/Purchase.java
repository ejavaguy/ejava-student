package ejava.jpa.example.validation;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import static javax.persistence.CascadeType.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

@Entity
@Table(name="VALIDATION_PURCHASE")
public class Purchase {
	@Id @GeneratedValue
	private int id;
	
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull(message="purchase requires a date")
	@Past(message="future purchases not allowed")
	private Date date;
	
	@OneToMany(fetch=FetchType.LAZY, cascade={PERSIST, REMOVE, DETACH})
	@JoinColumn(name="PURCHASE_ID")
	@Valid
	private Set<PurchaseItem> items;

	public int getId() { return id; }

	public Date getDate() { return date; }
	public Purchase setDate(Date date) {
		this.date = date;
		return this;
	}

	public Set<PurchaseItem> getItems() {
		if (items==null) {
			items = new HashSet<PurchaseItem>();
		}
		return items; 
	}
	public Purchase setItems(Set<PurchaseItem> items) {
		this.items = items;
		return this;
	}
	public Purchase addItem(PurchaseItem item) {
		getItems().add(item);
		return this;
	}
	
	public BigDecimal getTotal() {
		BigDecimal total = new BigDecimal(0);
		for (PurchaseItem item : getItems()) {
			total.add(item.getTotal());
		}
		return total;
	}
	
	@Override
	public String toString() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		NumberFormat nf = NumberFormat.getCurrencyInstance();
		StringBuilder text = new StringBuilder();
		text.append("#" + id);
		text.append(date==null?null : df.format(date));
		text.append("\n");
		for (PurchaseItem item : getItems()) {
			text.append(item.toString()).append("\n");
		}
		text.append("total=").append(nf.format(getTotal()));
		
		return text.toString();
	}
	
}
