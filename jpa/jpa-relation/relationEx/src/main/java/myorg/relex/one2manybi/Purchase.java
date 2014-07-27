package myorg.relex.one2manybi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
/**
 * This class provides an example of the one/inverse side of a one-to-many, bi-directional 
 * relationship realized through a join-table mapped from the owning/many side.
 */
@Entity
@Table(name="RELATIONEX_PURCHASE")
public class Purchase {
	@Id @GeneratedValue
	private int id;
	
	@OneToMany(
			mappedBy="purchase", 
			cascade={CascadeType.PERSIST, CascadeType.DETACH},
			orphanRemoval=true
		)
	private List<SaleItem> items;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false, updatable=false)
	private Date date;
	
	protected Purchase() {}
	public Purchase(Date date) {
		this.date = date;
	}

	public int getId() { return id; }
	public Date getDate() { return date; }
	public List<SaleItem> getItems() {
		if (items == null) {
			items = new ArrayList<SaleItem>();
		}
		return items;
	}
	public Purchase addItem(SaleItem item) {
		getItems().add(item);
		return this;
	}
}
