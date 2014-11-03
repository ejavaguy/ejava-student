package ejava.examples.ejbwar.customer.client;

import ejava.examples.ejbwar.customer.bo.Customer;
import ejava.examples.ejbwar.customer.bo.Customers;

/**
 * Defines an interface to the customer business logic using JAX-RS
 * resources.
 */
public interface CustomerClient {
	Customer addCustomer(Customer customer) throws Exception;
	Customers findCustomersByName(String firstName, String lastName, int offset, int limit) throws Exception;
	Customer getCustomer(int id) throws Exception;
	boolean deleteCustomer(int id) throws Exception;
}
