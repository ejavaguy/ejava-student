package ejava.projects.esales.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * This is an example eSales Account class. It will use full JPA annotations
 * to define the mappings to the database. We could have also used an orm.xml
 * file supplied by the DAO.
 * 
 * @author jcstaff
 *
 */
@SuppressWarnings("serial")
@Entity @Table(name="ESALES_ACCT")
public class Account implements Serializable {
	private String userId;
	private String firstName;
	private List<Address> addresses = new ArrayList<Address>();
	

	protected Account() {}   //JPA requires a no-arg ctor
	public Account(String userId) {
		setUserId(userId);
	}
	public Account(String userId, String firstName, List<Address> addresses) {
		setUserId(userId);
		this.firstName = firstName;
		this.addresses = addresses;
	}
	
	@Id @Column(name="USER_ID")
	public String getUserId() {
		return userId;
	}
	private void setUserId(String userId) {
		this.userId = userId;
	}

	@Column(name="FIRST_NAME")
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@OneToMany(cascade=CascadeType.ALL)
	@JoinTable(name="ESALES_ACCT_ADDRESS_LINK",
			joinColumns=@JoinColumn(name="USER_ID"),
			inverseJoinColumns=@JoinColumn(name="ADDRESS_ID"))
	public List<Address> getAddresses() {
		return addresses;
	}
	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}
	
	public String toString() {
		StringBuilder text = new StringBuilder();
		text.append("userId=" + userId);
		text.append(", firstName=" + firstName);
		text.append(", addresses={");
		for (Address a : addresses) {
			text.append("{" + a + "}, ");
		}
		text.append("}");
		return text.toString();
	}
}
