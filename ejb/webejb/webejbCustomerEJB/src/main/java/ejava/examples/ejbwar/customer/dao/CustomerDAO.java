package ejava.examples.ejbwar.customer.dao;

import java.util.List;

import ejava.examples.ejbwar.customer.bo.Customer;

/**
 * This interface defines basic DAO capabilities for the customer domain.
 */
public interface CustomerDAO {
	void createCustomer(Customer customer);
	List<Customer> findCustomerByName(String firstName, String lastName,
			int offset, int limit);
	Customer getCustomer(int id);
	Customer update(Customer customer);
	void deleteCustomer(Customer customer);
}