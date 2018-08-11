package ejava.examples.ejbwar.inventory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.ejbwar.inventory.bo.Category;
import ejava.examples.ejbwar.inventory.bo.Product;
import ejava.examples.ejbwar.jaxrs.JSONUtils;

public abstract class MarshallingTestBase {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected abstract <T> String marshal(T object) throws Exception;
    protected abstract <T> T unmarshal(String string, Class<T> type) throws Exception;
    
    protected void assertProductEquals(Product p, Product p2) {
        assertEquals("unexpected id", p.getId(), p2.getId());
        assertEquals("unexpected name", p.getName(), p2.getName());
        assertEquals("unexpected price", p.getPrice(), p2.getPrice());
        assertEquals("unexpected quantity", p.getQuantity(), p2.getQuantity());  
        assertNotNull("original product did not have a protected property", p.getProtectedValue());
        assertNull("result product has protected property", p2.getProtectedValue());
    }

    
    @Test
    public void product() throws Exception {
        Product p = new Product("soup", 16, 1.99);
        p.setProtectedValue("this is a secret!!!");
        
        logger.info("starting object {}", p);
        String wireFormat = marshal(p);
        logger.info("on the wire format=\n{}", wireFormat);
        
        Product p2 = unmarshal(wireFormat, Product.class);
        logger.info("resulting object {}", p2);
        assertProductEquals(p, p2);
    }
    
    @Test
    public void category() throws Exception {
        Category c = new Category("veggies");
        c.getProducts().add(new Product("beans", 10, 2.0).withProtectedValue("A"));
        c.getProducts().add(new Product("brocolli", 5, 1.0).withProtectedValue("B"));
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
