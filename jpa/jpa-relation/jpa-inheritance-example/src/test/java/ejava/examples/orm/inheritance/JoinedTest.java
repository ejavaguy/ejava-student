package ejava.examples.orm.inheritance;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ejava.examples.orm.inheritance.annotated.Customer;
import ejava.examples.orm.inheritance.annotated.Employee;
import ejava.examples.orm.inheritance.annotated.Person;

/**
 * This class provides a demonstration of a class hierachy that uses a 
 * class for each class in the inheritance hierarchy.
 */
public class JoinedTest extends DemoBase {
    
	@Before
    public void setUp() throws Exception {
        @SuppressWarnings("unchecked")
        List<Person> people = 
            em.createQuery("select p from Person p").getResultList();
        for(Person p: people) {
            em.remove(p);
        }
        em.flush();
        em.getTransaction().commit();
        em.getTransaction().begin();
    }

    @Test
    public void testTablePerClassCreate() {
        log.info("testTablePerClassCreate");
        
        ejava.examples.orm.inheritance.annotated.Employee employee = new Employee();
        employee.setFirstName("john");
        employee.setLastName("doe");
        employee.setHireDate(new Date());
        employee.setPayrate(10.00);
        em.persist(employee);
        
        ejava.examples.orm.inheritance.annotated.Customer customer = new Customer();
        customer.setFirstName("jane");
        customer.setLastName("johnson");
        customer.setRating(Customer.Rating.SILVER);
        em.persist(customer);
        
        em.flush();
        em.clear();
        assertFalse("employee still managed", em.contains(employee));
        assertFalse("customer still managed", em.contains(customer));
        
        List<Person> people = em.createQuery("select p from Person p", Person.class).getResultList();
        assertTrue("unexpected number of people:" + people.size(),
                people.size() == 2);
        for(Person p: people) {
            log.info("person found:" + p);
        }        
        
        //query specific tables for columns
        int rows = em.createNativeQuery(
                "select ID, FIRSTNAME, LASTNAME from ORMINH_PERSON")
                .getResultList().size();
        assertEquals("unexpected number of person rows:" + rows, 2, rows);
        rows = em.createNativeQuery(
                "select ID, RATING from ORMINH_CUSTOMER")
                .getResultList().size();
        assertEquals("unexpected number of customer rows:" + rows, 1, rows);
        rows = em.createNativeQuery(
                "select ID, PAYRATE, HIREDATE from ORMINH_EMPLOYEE")
                .getResultList().size();
        assertEquals("unexpected number of employee rows:" + rows, 1, rows);
    }
}
