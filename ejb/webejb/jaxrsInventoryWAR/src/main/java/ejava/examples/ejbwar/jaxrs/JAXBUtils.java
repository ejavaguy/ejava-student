package ejava.examples.ejbwar.jaxrs;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

@Provider
@Consumes(MediaType.APPLICATION_XML)
public class JAXBUtils implements ContextResolver<JAXBContext> {
    private static JAXBUtils instance;
    
    @Override
    public JAXBContext getContext(Class<?> type) {
        try {
            JAXBContext jbx=JAXBContext.newInstance(type);
            return jbx;
        } catch (JAXBException ex) {
            throw new IllegalStateException("error resolving JAXBContext for type: " + type, ex);
        }
    }

    private static JAXBUtils getInstance() {
        if (instance==null) {
            instance=new JAXBUtils();
        }
        return instance;
    }

    public static <T> String marshall(Object object) throws JAXBException {
        if (object==null) {
            return "";
        }
        
        JAXBContext jbx = getInstance().getContext(object.getClass());
        Marshaller marshaller = jbx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter writer = new StringWriter();
        marshaller.marshal(object, writer);
        return writer.toString();
    }
    
    public static <T> T unmarshall(String string, Class<T> type) throws JAXBException {
        if (string==null || string.isEmpty()) {
            return null;
        }
        return unmarshall(new ByteArrayInputStream(string.getBytes()), type);
    }
    
    public static <T> T unmarshall(InputStream is, Class<T> type) throws JAXBException {
        if (is==null) {
            return null;
        }
        
        JAXBContext jbx = getInstance().getContext(type);
        Unmarshaller unmarshaller = jbx.createUnmarshaller();
        @SuppressWarnings("unchecked")
        T object = (T) unmarshaller.unmarshal(is);
        return object;
    }
}
