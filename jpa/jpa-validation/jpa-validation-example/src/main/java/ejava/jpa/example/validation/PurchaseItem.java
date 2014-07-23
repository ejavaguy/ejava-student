package ejava.jpa.example.validation;

import java.math.BigDecimal;
import java.text.NumberFormat;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name="VALIDATION_PURCHASEITEM")
public class PurchaseItem {
	@Id @GeneratedValue
	private int id;
	
	@Column(length=20, nullable=false)
	@NotNull(message="item description is required")
	@Size(max=20, message="description too large")
	private String description;
	
	@Column(precision=7, scale=2, nullable=false)
	@NotNull(message="item amount is required")
	private BigDecimal amount;
	
	@Column(nullable=false)
	@Min(value=1, message="item count is required")
	private int count;

	public int getId() { return id; }

	public String getDescription() { return description; }
	public PurchaseItem setDescription(String description) {
		this.description = description;
		return this;
	}

	public BigDecimal getAmount() { return amount; }
	public PurchaseItem setAmount(BigDecimal amount) {
		this.amount = amount;
		return this;
	}

	public int getCount() { return count; }
	public PurchaseItem setCount(int count) {
		this.count = count;
		return this;
	}
	
	public BigDecimal getTotal() {
		return amount.multiply(new BigDecimal(count));
	}
	
	@Override
	public String toString() {
		NumberFormat nf = NumberFormat.getCurrencyInstance();
		return String.format("%20s %dx %d", description, count, nf.format(getTotal()));
	}
}
