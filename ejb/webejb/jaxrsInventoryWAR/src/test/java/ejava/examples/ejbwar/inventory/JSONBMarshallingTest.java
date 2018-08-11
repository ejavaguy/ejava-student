package ejava.examples.ejbwar.inventory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.ejbwar.inventory.bo.Category;
import ejava.examples.ejbwar.inventory.bo.Product;
import ejava.examples.ejbwar.jaxrs.JSONUtils;

/**
 * This class provides a quick sanity check of the JSON marshaling of a 
 * domain POJOs to/from JSON based on JSON-B annotation bindings.
 */
public class JSONBMarshallingTest extends MarshallingTestBase {
    private static final Logger logger = LoggerFactory.getLogger(JSONBMarshallingTest.class);
    
    @Test
    public void product() throws JAXBException {
        Product p = new Product("soup", 16, 1.99);
        
        String wireFormat = JSONUtils.marshal(p);
        logger.info("on the wire format=\n{}", wireFormat);
        
        Product p2 = JSONUtils.unmarshal(wireFormat, Product.class);
        assertProductEquals(p, p2);
    }
    
    @Test
    public void category() throws JAXBException {
        Category c = new Category("veggies");
        c.getProducts().add(new Product("beans", 10, 2.0));
        c.getProducts().add(new Product("brocolli", 5, 1.0));
        String wireFormat = JSONUtils.marshal(c);
        logger.info("on the wire format=\n{}", wireFormat);
        
        Category c2 = JSONUtils.unmarshal(wireFormat, Category.class);
        assertEquals("unexpected id", c.getId(), c2.getId());
        assertEquals("unexpected id", c.getName(), c2.getName());
        assertEquals("unexpected id", c.getProductCount(), c2.getProductCount());
        
        Map<String, Product> productMap = c2.getProducts()
                                            .stream()
                                            .collect(Collectors.toMap(Product::getName, x->x));
        for (Product p: c.getProducts()) {
            Product p2 = productMap.get(p.getName());
            assertNotNull("product not found: " + p.getName(), p2);
            assertProductEquals(p, p2);
        }
    }

}


