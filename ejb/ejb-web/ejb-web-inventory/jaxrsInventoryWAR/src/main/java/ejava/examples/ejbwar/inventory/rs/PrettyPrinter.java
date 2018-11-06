package ejava.examples.ejbwar.inventory.rs;

import java.io.IOException;

import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Put this class in your JAX-RS application to have XML output formatted
 * so that it is easier to read.
 */
@Provider
@Produces(MediaType.APPLICATION_XML)
public class PrettyPrinter implements MessageBodyWriter<Object> {

    @Context protected Providers providers;

    public long getSize(Object object, Class<?> type, Type genericType,
            Annotation[] methodAnnotations, MediaType mediaType) {
        return -1; //says we don't know
    }

    /**
     * Return true for any JAXB class
     */
    public boolean isWriteable(Class<?> type, Type genericType, 
            Annotation[] methodAnnotations, MediaType mediaType) {
        if (MediaType.APPLICATION_XML_TYPE == mediaType) {
            for(Class<?> clazz = type;clazz != null; clazz=clazz.getSuperclass()) {
                if (clazz.isAnnotationPresent(XmlRootElement.class)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Write the provided JAXB object using formatted output.
     */
    public void writeTo(Object object, Class<?> type, Type genericType,
            Annotation[] methodAnnotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream os)
            throws IOException, WebApplicationException {

    	//get a JAXBContext that can marshall the JAXBObject
        JAXBContext ctx = null;
        ContextResolver<JAXBContext> resolver = 
            providers.getContextResolver(JAXBContext.class, MediaType.APPLICATION_XML_TYPE);
        if (resolver != null) {
            ctx = resolver.getContext(type);
        }
        
        //if none is found, try a default one
        if (ctx == null) {
            try { ctx = JAXBContext.newInstance(type); } 
            catch (JAXBException ex) {throw new RuntimeException("error creating ad-hoc jaxbContext", ex);}
        }

        //marshall the object to the provided stream
        try {
            Marshaller marshaller = ctx.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(object, os);
            os.flush();
        } catch (JAXBException ex) {
            throw new RuntimeException("error marshalling XML", ex);
        } finally {}
    }
}