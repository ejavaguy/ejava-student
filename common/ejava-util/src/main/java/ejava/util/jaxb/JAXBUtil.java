package ejava.util.jaxb;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class JAXBUtil {
    public static <T> String marshal(T object) {
        try {
            return marshalThrows(object);
        } catch (JAXBException ex) {
            return null;
        }
    }
    
    public static <T> String marshalThrows(T object) throws JAXBException {
        if (object==null) {
            return null;
        }

        JAXBContext jbx = JAXBContext.newInstance(object.getClass());
        Marshaller marshaller = jbx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter writer = new StringWriter();
        marshaller.marshal(object, writer);
        return writer.toString();        
    }
    
    public static <T> T unmarshal(String string, Class<T> type) {
        try {
            return unmarshalThrows(string, type);
        } catch (JAXBException ex) {
            return null;
        }
    }
    
    public static <T> T unmarshal(InputStream is, Class<T> type) {
        try {
            return unmarshalThrows(is, type);
        } catch (JAXBException ex) {
            return null;
        }        
    }
    
    public static <T> T unmarshalThrows(String string, Class<T> type) throws JAXBException {
        if (string==null || string.isEmpty()) {
            return null;
        }
        return unmarshalThrows(new ByteArrayInputStream(string.getBytes()), type);
    }

    public static <T> T unmarshalThrows(InputStream is, Class<T> type) throws JAXBException {
        if (is==null) {
            return null;
        }

        JAXBContext jbx = JAXBContext.newInstance(type);
        Unmarshaller unmarshaller = jbx.createUnmarshaller();
        @SuppressWarnings("unchecked")
        T object = (T) unmarshaller.unmarshal(is);
        return object;
    }

}
