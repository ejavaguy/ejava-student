package ejava.examples.ejbwar.inventory.bo;

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

/**
 * This class represents a specific product. It has been mapped to both
 * the DB and XML.
 */
@XmlRootElement(name="product", namespace=InventoryRepresentation.NAMESPACE)
@XmlType(name="product", namespace=InventoryRepresentation.NAMESPACE, propOrder={
		"name",
		"quantity",
		"price"
})
@XmlAccessorType(XmlAccessType.PROPERTY)

@Entity
@Table(name="JAXRSINV_PRODUCT")
@NamedQueries({
	@NamedQuery(name=Product.FIND_BY_NAME, 
			query="select p from Product p where name like :criteria")
})
public class Product extends InventoryRepresentation {
	private static final long serialVersionUID = -4058695470696405277L;
	public static final String FIND_BY_NAME = "Inventory.findProductByName";

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID")
	private int id;
	
	@Column(name="NAME", nullable=false)
	private String name;
	
	@Column(name="QTY", nullable=true)
	private Integer quantity;
	
	@Column(name="PRICE", nullable=true)
	private Double price;

	public Product() {}
	public Product(String name, Integer quantity, Double price) {
		this.name=name;
		this.quantity=quantity;
		this.price=price;
	}
	public Product(String name) {
		this(name, null, null);
	}
	
	@XmlAttribute(required=true)
	public int getId() { return id;}
	public void setId(int id) {
		this.id = id;
	}

	@XmlAttribute(required=true)
	public String getName() { return name; }
	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(required=false)
	public Integer getQuantity() { return quantity; }
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@XmlElement(required=false)
	public Double getPrice() { return price; }
	public void setPrice(Double price) {
		this.price = price;
	}
}
