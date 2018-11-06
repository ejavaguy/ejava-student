package ejava.examples.ejbwar.customer.ejb;

import ejava.examples.ejbwar.customer.bo.Customer;
import ejava.examples.ejbwar.customer.bo.Customers;

/**
 * This interface defines the business interface for the customer management
 * EJB.
 */
public interface CustomerMgmt {
	Customer addCustomer(Customer customer);
	Customers findCustomersByName(String firstName, String lastName, int offset, int limit);
	Customer getCustomer(int id);
	void deleteCustomer(int id);
}
