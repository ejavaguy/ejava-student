package ejava.examples.ejbwar.inventory.bo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents a product category which has a many-to-many 
 * relationship with product. It has been mapped to the DB and XML.
 */
@XmlRootElement(name="catageory", namespace=InventoryRepresentation.NAMESPACE)
@XmlType(name="Category", namespace=InventoryRepresentation.NAMESPACE)
@XmlAccessorType(XmlAccessType.PROPERTY)

@Entity
@Table(name="JAXRSINV_CATEGORY")
@NamedQueries({
	@NamedQuery(name=Category.FIND_BY_NAME, 
		query="select c from Category c where c.name like :criteria"),
	@NamedQuery(name=Category.FIND_BY_PRODUCT, 
		query="select c from Category c " +
				"where :product member of c.products" )
})
public class Category extends InventoryRepresentation {
	private static final long serialVersionUID = 2367549577678745828L;
	public static final String FIND_BY_NAME = "Inventory.findCategoryByName";
	public static final String FIND_BY_PRODUCT = "Inventory.findCategoryByProduct";;
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID")
	private int id;
	
	@Column(name="NAME", unique=true)
	private String name;
	
	@Transient
	private Integer productCount;
	
	@ManyToMany
	@JoinTable(name="JAXRSINV_PROD_CAT")
	private List<Product> products=new ArrayList<Product>();

	public Category() {}
	public Category(String name) {
		this.name=name;
	}
	
	@XmlAttribute(required=true)
	public int getId() { return id; }
	public void setId(int id) {
		this.id = id;
	}
	
	@XmlAttribute(required=true)
	public String getName() { return name; }
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(required=true)
	public int getProductCount() { return productCount!=null ? productCount :products.size();}
	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}
	
	@XmlElementWrapper(name="products")
	@XmlElement(name="product")
	public List<Product> getProducts() { return products; }
	public void setProducts(List<Product> products) {
		this.products = products;
	}
}
