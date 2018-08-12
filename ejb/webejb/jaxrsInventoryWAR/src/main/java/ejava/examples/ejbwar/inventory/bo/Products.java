package ejava.examples.ejbwar.inventory.bo;

import java.util.ArrayList;

import java.util.List;

import javax.json.bind.annotation.JsonbAnnotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import ejava.examples.ejbwar.inventory.bo.InventoryRepresentation;

/**
 * This class is used to represent a collection of products to/from the 
 * server. It also contains some of the collection metadata.
 */
@XmlRootElement(name="products", namespace=InventoryRepresentation.NAMESPACE)
@XmlType(name="Products", namespace=InventoryRepresentation.NAMESPACE)
@XmlAccessorType(XmlAccessType.PROPERTY)

@JsonbAnnotation //used to combat JAXB annotations from picking Jackson2 provider
public class Products extends InventoryRepresentation {
	private static final long serialVersionUID = 8409120005599383060L;
	private int offset;
	private int limit;
	private List<Product> products=new ArrayList<Product>();
	
	public Products() {}
	public Products(List<Product> products, int offset, int limit) {
		this.products = products;
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
		return products.size();
	}
	public void setCount(int count) {}
	
	
	@XmlElement
	public List<Product> getProducts() {
		return products;
	}
	public void setProducts(List<Product> products) {
		this.products = products;
	}
	
	
}
