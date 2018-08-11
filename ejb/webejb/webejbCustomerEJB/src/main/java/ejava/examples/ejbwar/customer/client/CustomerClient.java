package ejava.examples.ejbwar.customer.client;

import ejava.examples.ejbwar.customer.bo.Customer;
import ejava.examples.ejbwar.customer.bo.Customers;

/**
 * Defines an interface to the customer business logic using JAX-RS
 * resources.
 */
public interface CustomerClient {
	Customer addCustomer(Customer customer);
	Customers findCustomersByName(String firstName, String lastName, int offset, int limit);
	Customer getCustomer(int id);
	boolean deleteCustomer(int id);
}
