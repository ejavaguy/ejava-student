package ejava.projects.eleague.xml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.Callable;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.sun.xml.bind.IDResolver;

/**
 * This class will read in Java objects from a specified XML file. These objects
 * can be used to create ingest data for projects.
 */
public class ELeagueParser {
	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger(ELeagueParser.class);
	protected XMLInputFactory xmlif = XMLInputFactory.newInstance();
	protected Unmarshaller um;
	protected XMLStreamReader xmlr;
	public static final String SAMPLE_FILE = "xml/eSales-10.xml";

	/**
	 * Pass in the JAXB class that represents the root node of the document and
	 * an InputStream for the document to parse.
	 * 
	 * @param rootType
	 *            - the class of the root type
	 * @param is
	 *            - am input stream with document to parse
	 * @throws JAXBException
	 * @throws XMLStreamException
	 */
	public ELeagueParser(Class<?> rootType, InputStream is)
			throws JAXBException, XMLStreamException {
		JAXBContext jaxbContext = JAXBContext.newInstance(rootType);
		um = jaxbContext.createUnmarshaller();
		xmlif = XMLInputFactory.newInstance();
		xmlr = xmlif.createXMLStreamReader(is);

		// This (anonymous) class is a near replicate of sun's DefaultIDResolver
		// except that they added a clear() of the idmap within startDocument()
		// that prevents the unmarshaller from being called multiple times.
		IDResolver idResolver = new IDResolver() {
			private HashMap<String, Object> idmap = null;

			@Override
			public Callable<?> resolve(final String id, 
					@SuppressWarnings("rawtypes") Class targetType)
					throws SAXException {
				return new Callable<Object>() {
					public Object call() throws Exception {
						if (idmap == null)
							return null;
						return idmap.get(id);
					}
				};
			}

			@Override
			public void bind(String id, Object obj) throws SAXException {
				if (idmap == null)
					idmap = new HashMap<String, Object>();
				idmap.put(id, obj);
			};
		};
		um.setProperty(IDResolver.class.getName(), idResolver);
	}

	public void setSchema(InputStream schema) throws SAXException {
		SchemaFactory sf = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schemaObject = sf.newSchema(new StreamSource(schema));
		um.setSchema(schemaObject);
	}

	private boolean contains(String elements[], String localName) {
		for (String element : elements) {
			if (element.equalsIgnoreCase(localName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method will return either the object or null if we hit the end of
	 * stream before getting another instance. Note that only the local-name is
	 * being used. That won't work to great when two namespaces declare a common
	 * local-name. Should be easily fixable when needed.
	 * 
	 * @param elements an vararg of element names you wish to pull from document
	 * @return The next element that matches one of the element names in elements.
	 * @throws XMLStreamException
	 * @throws JAXBException
	 */
    @SuppressWarnings("rawtypes")
	public Object getObject(String... elements) throws XMLStreamException,
			JAXBException {
		xmlr.next();
		while (xmlr.hasNext()) {
			if (xmlr.isStartElement()
					&& contains(elements, xmlr.getName().getLocalPart())) {
				Object object = um.unmarshal(xmlr);
				return (object instanceof JAXBElement) ? ((JAXBElement) object)
						.getValue() : object;
			}
			xmlr.next();
		}
		return null;
	}

	public static InputStream getSampleData() {
		return Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(SAMPLE_FILE);
	}
}
