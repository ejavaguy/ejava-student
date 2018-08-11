package ejava.examples.ejbwar.inventory;

import ejava.examples.ejbwar.jaxrs.JSONUtils;

/**
 * This class provides a quick sanity check of the JSON marshaling of a 
 * domain POJOs to/from JSON based on JSON-B annotation bindings.
 */
public class JSONBMarshallingTest extends MarshallingTestBase {

    @Override
    protected <T> String marshal(T object) throws Exception {
        return JSONUtils.marshal(object);
    }

    @Override
    protected <T> T unmarshal(String string, Class<T> type) throws Exception {
        return JSONUtils.unmarshal(string, type);
    }
}


