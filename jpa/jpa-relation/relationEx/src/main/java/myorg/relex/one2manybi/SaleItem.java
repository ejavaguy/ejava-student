package myorg.relex.one2manybi;

import java.math.BigDecimal;

import javax.persistence.*;

/**
 * This class provides and example of the many/owning side of a many-to-one, bi-directional 
 * relationship that is realized using a join-table.
 */
@Entity
@Table(name="RELATIONEX_SALEITEM")
public class SaleItem {
	@Id @GeneratedValue
	private int id;
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@JoinTable(
		name="RELATIONEX_SALEITEM_PURCHASE", 
		joinColumns=@JoinColumn(name="SALEITEM_ID"),
		inverseJoinColumns=@JoinColumn(name="PURCHASE_ID")
	)
	private Purchase purchase;
	
	@Column(length=16)
	private String name;
	@Column(precision=5, scale=2)
	private BigDecimal price;
	
	protected SaleItem() {}
	public SaleItem(Purchase purchase) {
		this.purchase = purchase;
	}

	public int getId() { return id; }
	
	public Purchase getPurchase() { return purchase; }
	public void setPurchase(Purchase purchase) {
		this.purchase = purchase;
	}

	public String getName() { return name; }
	public void setName(String name) {
		this.name = name;
	}
	
	public double getPrice() { return price==null? 0 : price.doubleValue(); }
	public void setPrice(double price) {
		this.price = new BigDecimal(price);
	}
}
