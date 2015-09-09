package ejava.projects.esales.xml;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import com.sun.xml.bind.IDResolver;

/**
 * This class will read in Java objects from a specified XML file. These 
 * objects can be used to create ingest data for projects.
 * 
 * @author jcstaff
 *
 */
public class ESalesParser {
    @SuppressWarnings("unused")
    private Log log = LogFactory.getLog(ESalesParser.class);
    protected XMLInputFactory xmlif = XMLInputFactory.newInstance();
    protected Unmarshaller um;
    protected XMLStreamReader xmlr;
    public static final String SAMPLE_FILE = "xml/eSales-10.xml";
   
    /**
     * Pass in the JAXB class that represents the root node of the document
     * and an InputStream for the document to parse.
     * 
     * @param rootType - the class of the root type
     * @param is - am input stream with document to parse
     * @throws JAXBException
     * @throws XMLStreamException
     */
    public ESalesParser(Class<?> rootType, InputStream is) 
    
        throws JAXBException, XMLStreamException {
        JAXBContext jaxbContext = JAXBContext.newInstance(rootType);
        um = jaxbContext.createUnmarshaller();
        xmlif = XMLInputFactory.newInstance();
        xmlr = xmlif.createXMLStreamReader(is);

        //This (anonymous) class is a near replicate of sun's DefaultIDResolver
        //except that they added a clear() of the idmap within startDocument()
        //that prevents the unmarshaller from being called multiple times.
        IDResolver idResolver = new IDResolver() {
            private HashMap<String,Object> idmap = null;

			@SuppressWarnings("rawtypes")
			@Override
			public Callable<?> resolve(final String id, Class targetType) throws SAXException {
                return new Callable() {
                    public Object call() throws Exception {
                        if(idmap==null)     return null;
                        return idmap.get(id);
                    }
                };
			}
			
			@Override
			public void bind(String id, Object obj) throws SAXException {
                if(idmap==null)     idmap = new HashMap<String,Object>();
                idmap.put(id,obj);
			};
		};
        um.setProperty(IDResolver.class.getName(), idResolver);    
    }
    
    public void setSchema(InputStream schema) throws SAXException {
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schemaObject = sf.newSchema(new StreamSource(schema));
        um.setSchema(schemaObject);
    }
    
    private boolean contains(String elements[], String localName) {
        for(String element: elements) {
            if (element.equalsIgnoreCase(localName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * This method will return either the object or null if we hit the end
     * of stream before getting another instance. Note that only the local-name
     * is being used. That won't work to great when two namespaces declare 
     * a common local-name. Should be easily fixable when needed.
     * 
     * @param element
     * @return
     * @throws XMLStreamException
     * @throws JAXBException
     */
    public Object getObject(String...elements) 
        throws XMLStreamException, JAXBException {
        xmlr.next();
        while (xmlr.hasNext()) {
            if (xmlr.isStartElement() && 
                    contains(elements, xmlr.getName().getLocalPart())) {
                Object object = um.unmarshal(xmlr);
                return (object instanceof JAXBElement) ?
                    ((JAXBElement<?>)object).getValue() : object;
            }
            xmlr.next();
        }
        return null;        
    }
    
    public static InputStream getSampleData() {
        return Thread.currentThread()
           .getContextClassLoader()
           .getResourceAsStream(SAMPLE_FILE);
    }
}
