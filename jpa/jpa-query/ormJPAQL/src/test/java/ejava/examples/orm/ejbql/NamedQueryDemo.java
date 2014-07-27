package ejava.examples.orm.ejbql;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import ejava.examples.orm.ejbql.annotated.Customer;
import ejava.examples.orm.ejbql.annotated.Sale;

public class NamedQueryDemo extends DemoBase {

    @SuppressWarnings("unchecked")
    @Test
    public void testNamedQuery() {
        log.info("*** testNamedQuery() ***");
        
        Customer customer = (Customer)
            em.createNamedQuery("getCustomersByName")
              .setParameter("first", "cat")
              .setParameter("last", "inhat")
              .getResultList()
              .get(0);
        assertNotNull("no customer found", customer);
        log.info("found customer:" + customer);
        
        Query query = em.createNamedQuery("getCustomerPurchases");
        List<Sale> sales = query.setParameter("custId", customer.getId())
                                .getResultList();
        assertTrue("no sales found", sales.size() > 0);
        for (Sale s: sales) {
            log.info("found sale:" + s);
        }
    }
}
