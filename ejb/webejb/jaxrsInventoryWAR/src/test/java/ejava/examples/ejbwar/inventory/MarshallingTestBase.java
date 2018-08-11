package ejava.examples.ejbwar.inventory;

import static org.junit.Assert.assertEquals;

import ejava.examples.ejbwar.inventory.bo.Product;

public class MarshallingTestBase {

    protected void assertProductEquals(Product p, Product p2) {
        assertEquals("unexpected id", p.getId(), p2.getId());
        assertEquals("unexpected name", p.getName(), p2.getName());
        assertEquals("unexpected price", p.getPrice(), p2.getPrice());
        assertEquals("unexpected quantity", p.getQuantity(), p2.getQuantity());        
    }

}
