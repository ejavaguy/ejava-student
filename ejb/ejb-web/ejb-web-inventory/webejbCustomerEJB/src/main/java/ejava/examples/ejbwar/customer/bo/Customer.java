package ejava.examples.ejbwar.customer.bo;

import javax.json.bind.annotation.JsonbAnnotation;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="customer", namespace=CustomerRepresentation.NAMESPACE)
@XmlType(name="Customer", namespace=CustomerRepresentation.NAMESPACE, propOrder={
		"firstName",
		"lastName"
})
@XmlAccessorType(XmlAccessType.PROPERTY)

//need at least one Jsonb annotation to tell provider this is JSON-B
@JsonbAnnotation

@Entity
@Table(name="WEBEJB_CUSTOMER")
@NamedQueries({
	@NamedQuery(name=Customer.FIND_BY_NAME, 
		query="select c from Customer c where " +
				"c.firstName like :firstName and " +
				"c.lastName like :lastName"),
})
public class Customer extends CustomerRepresentation {
	private static final long serialVersionUID = 2886191800865680970L;
	public static final String FIND_BY_NAME="Customer.findByName";
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID")
	private int id;
	
	@Column(name="FIRST_NAME", nullable=false)
	private String firstName;
	
	@Column(name="LAST_NAME", nullable=false)
	private String lastName;
	
	public Customer() {}
	public Customer(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	@XmlAttribute
	public int getId() { return id; }
	public void setId(int id) {
		this.id = id;
	}
	
	@XmlElement
	public String getFirstName() { return firstName; }
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	@XmlElement
	public String getLastName() { return lastName; }
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
