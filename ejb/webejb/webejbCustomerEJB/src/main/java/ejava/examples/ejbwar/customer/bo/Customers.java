package ejava.examples.ejbwar.customer.bo;

import java.util.ArrayList;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class is used to represent a collection of customers to/from the 
 * server. It also contains some of the collection metadata.
 */
@XmlRootElement(name="customers", namespace=CustomerRepresentation.NAMESPACE)
@XmlType(name="Customers", namespace=CustomerRepresentation.NAMESPACE)
@XmlAccessorType(XmlAccessType.PROPERTY)

public class Customers extends CustomerRepresentation {
	private static final long serialVersionUID = 8938786129503381169L;
	private int offset;
	private int limit;
	private List<Customer> customers=new ArrayList<Customer>();
	
	public Customers() {}
	public Customers(List<Customer> customers, int offset, int limit) {
		this.customers = customers;
		this.offset = offset;
		this.limit = limit;
	}
	
	@XmlAttribute
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	@XmlAttribute
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	@XmlAttribute
	public int getCount() {
		return customers.size();
	}
	public void setCount(int count) {}
	
	
	@XmlElement
	public List<Customer> getCustomers() {
		return customers;
	}
	public void setCategories(List<Customer> customers) {
		this.customers = customers;
	}
	
	
}
