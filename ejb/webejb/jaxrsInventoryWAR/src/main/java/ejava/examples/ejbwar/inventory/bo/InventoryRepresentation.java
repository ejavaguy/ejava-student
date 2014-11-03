package ejava.examples.ejbwar.inventory.bo;

import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
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
	
	/**
	 * Our toString() implementation will take advantage of the JAXB defintion
	 * for the class and marshall the object as an XML string.
	 */
	@Override
	public String toString() {
		try {
			JAXBContext jbx = JAXBContext.newInstance(getClass());
			Marshaller marshaller = jbx.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			StringWriter writer = new StringWriter();
			marshaller.marshal(this, writer);
			return writer.toString();
		} catch (JAXBException ex) {
			throw new RuntimeException("unexpected JAXB error marshalling object:", ex);
		}
	}
	
	/**
	 * This method will unmarshall the specified class from the provided 
	 * XML input stream.
	 * @param clazz
	 * @param is
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T unmarshall(Class<T> clazz, InputStream is) {
		try {
			JAXBContext jbx = JAXBContext.newInstance(clazz);
			Unmarshaller marshaller = jbx.createUnmarshaller();
			return (T) marshaller.unmarshal(is);
		} catch (JAXBException ex) {
			throw new RuntimeException("unexpected JAXB error marshalling object:", ex);
		}
	}
}
