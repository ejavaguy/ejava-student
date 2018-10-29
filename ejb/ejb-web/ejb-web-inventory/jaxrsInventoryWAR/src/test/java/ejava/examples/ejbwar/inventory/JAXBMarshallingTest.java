package ejava.examples.ejbwar.inventory;

import javax.xml.bind.JAXBException;

import ejava.examples.ejbwar.jaxrs.JAXBUtils;

/**
 * This class provides a quick sanity check of the JAXB marshaling of a 
 * domain POJOs to/from XML based on JAXB annotation bindings.
 */
public class JAXBMarshallingTest extends MarshallingTestBase {
    @Override
    protected <T> String marshal(T object) throws JAXBException {
        return JAXBUtils.marshal(object);
    }

    @Override
    protected <T> T unmarshal(String string, Class<T> type) throws JAXBException {
        return JAXBUtils.unmarshal(string, type);
    }
}
