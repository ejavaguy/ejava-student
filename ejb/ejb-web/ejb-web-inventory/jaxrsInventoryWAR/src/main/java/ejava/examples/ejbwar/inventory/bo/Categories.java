package ejava.examples.ejbwar.inventory.bo;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import ejava.examples.ejbwar.inventory.bo.Category;
import ejava.examples.ejbwar.inventory.bo.InventoryRepresentation;

/**
 * This class is used to represent a collection of categories to/from the 
 * server. It also contains some of the collection metadata.
 */
@XmlRootElement(name="catageories", namespace=InventoryRepresentation.NAMESPACE)
@XmlType(name="Categories", namespace=InventoryRepresentation.NAMESPACE)
@XmlAccessorType(XmlAccessType.PROPERTY)

public class Categories extends InventoryRepresentation {
	private static final long serialVersionUID = 8938786129503381169L;
	private int offset;
	private int limit;
	private List<Category> categories=new ArrayList<Category>();
	
	public Categories() {}
	public Categories(List<Category> categories, int offset, int limit) {
		this.categories = categories;
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
		return categories.size();
	}
	public void setCount(int count) {}
	
	
	@XmlElement
	public List<Category> getCategories() {
		return categories;
	}
	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
	
	
}
