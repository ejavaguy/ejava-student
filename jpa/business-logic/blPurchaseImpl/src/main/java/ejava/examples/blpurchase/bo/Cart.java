package ejava.examples.blpurchase.bo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * The shopping cart contans all the items a user has cached for purchase.
 */
@Entity
@Table(name="BLPURCHASE_CART")
public class Cart {
	@Id
	private String email;
	
	@OneToOne
	@PrimaryKeyJoinColumn(referencedColumnName="email")
	private Account account;
	
	@ManyToMany
	private List<Product> products = new ArrayList<Product>();

	public Cart(){}
	public Cart(Account account) {
		this.email=account.getEmail();
		this.account=account;
	}

	public String getEmail() {
		return email;
	}
	protected void setEmail(String email) {
		this.email = email;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}
}
