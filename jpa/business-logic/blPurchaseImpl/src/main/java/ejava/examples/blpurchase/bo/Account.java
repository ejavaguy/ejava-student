package ejava.examples.blpurchase.bo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * An account is tracked 
 */
@Entity
@Table(name="BLPURCHASE_ACCOUNT")
@NamedQueries({
	@NamedQuery(name="blPurchasing.findAccountByEmail", 
			    query="select a from Account a where a.email=:email")
})
public class Account {
	public static final String FIND_BY_EMAIL="blPurchasing.findAccountByEmail";
	
	@Id @GeneratedValue
	private int id;
	
	@Column(nullable=false, unique=true)
	private String email;
	
	@Column(nullable=false)
	private String password;
	
	@Column(nullable=false)
	private String firstName;
	
	@Column(nullable=false)
	private String lastName;

	public Account(){}
	public Account(String email, String firstName, String lastName) {
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public int getId() {
		return id;
	}
	protected void setId(int id) {
		this.id = id;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("id=").append(id)
		       .append(", email=").append(email)
			   .append(", firstName=").append(firstName)
			   .append(", lastName=").append(lastName);
		return builder.toString();
	}
	
	
}
