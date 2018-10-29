package ejava.examples.ejbwar.inventory.bo;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTransient;
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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents a specific product. It has been mapped to both
 * the DB, XML, and JSON -- which is not that common to do and causes 
 * some conflict.
 * 
 * JAX-RS originally only had to support JAXB (i.e., @Xml annotations) and
 * each vendor added optional extensions to support alternate formats like
 * JSON. One common approach was to apply the Xml-centric JAXB bindings to 
 * JSON marshaling. That is what was done by the Jackson JSON marshaler.
 * Jackson also had annotations (@Json) that could override JAXB (@Xml) 
 * annotations. That is how things sat for < jee8.
 * 
 * With jee8 and jax-rs 2.1, JSON-B was created and support mandated. This 
 * example was written before full jee8 compliance. Thus it has been annotated 
 * with JSOB-B annotations (@Jsonb) and Jackson (@Json) annotations so that 
 * it will work with both jee7 and jee8 deployments. The client will always
 * be using JSON-B.
 * 
 * The use of JAXB annotations for Json does confuse the marshaling decision 
 * logic. So we have to make sure we have JSON-B annotations on the class,
 * and passed to the marshaling methods. Of course -- if your marshaled class
 * is a true DTO, then there is much less need for overrides.
 */
@XmlRootElement(name="product", namespace=InventoryRepresentation.NAMESPACE)
@XmlType(name="product", namespace=InventoryRepresentation.NAMESPACE, propOrder={
		"name",
		"quantity",
		"price"
})
@XmlAccessorType(XmlAccessType.PROPERTY)
@JsonbPropertyOrder({"id","name", "quantity", "price"})

@Entity
@Table(name="JAXRSINV_PRODUCT")
@NamedQueries({
	@NamedQuery(name=Product.FIND_BY_NAME, 
			query="select p from Product p where p.name like :criteria")
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

    @Column(name="PROTECTED_VALUE", length=36, nullable=false, updatable=false)
    private String protectedValue;

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

	@XmlAttribute(required=true, name="xmlName") //used both during marshal and unmarshal
	@JsonbProperty("jsonName") //for json-b -- used during toJson()
	@JsonProperty("jsonName")  //for jackson json -- needed when server not >=jee8 mode
	public String getName() { return name; }
    @JsonbProperty("jsonName") //used during fromJson()
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
	
	@XmlTransient
    @JsonbTransient
	public String getProtectedValue() { return protectedValue; }
    @JsonbTransient
    @JsonIgnore //for jackson json -- when < jee8 or overriding JAXB annotation 
	public void setProtectedValue(String protectedValue) {
        this.protectedValue = protectedValue;
    }
    public Product withProtectedValue(String string) {
        setProtectedValue(string);
        return this;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Product [id=").append(id)
                .append(", name=").append(name)
                .append(", quantity=").append(quantity)
                .append(", price=").append(price)
                .append(", protectedValue=")
                .append(protectedValue).append("]");
        return builder.toString();
    }
    
    
}
