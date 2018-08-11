package ejava.examples.ejbwar.inventory.bo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * This class provides base definition and helper methods for representations
 * within the inventory domain.
 */
@SuppressWarnings("serial")
@MappedSuperclass
public abstract class InventoryRepresentation implements Serializable {
	public static final String NAMESPACE = "http://webejb.ejava.info/inventory";
	
	@Version
	@Column(name="VERSION")
	private int version;
	
	/**
	 * This property is added to each entity so that we can have better 
	 * control over concurrent updates
	 * @return
	 */
	@XmlAttribute(required=true)
	public int getVersion() { return version; }
	public void setVersion(int version) {
		this.version = version;
	}
}
