package ejava.examples.ejbwar.customer.ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.ejbwar.customer.bo.Customer;
import ejava.examples.ejbwar.customer.bo.Customers;
import ejava.examples.ejbwar.customer.dao.CustomerDAO;

@Stateless
public class CustomerMgmtEJB implements CustomerMgmtLocal, CustomerMgmtRemote {
	private static final Logger log = LoggerFactory.getLogger(CustomerMgmtEJB.class);
	
	@Inject
	private CustomerDAO dao;
	
	@Override
	public Customer addCustomer(Customer customer) {
		log.debug(String.format("addCustomer(%s)", customer));
		dao.createCustomer(customer);
		return customer;
	}

	@Override
	public Customers findCustomersByName(String firstName, String lastName,
			int offset, int limit) {
		log.debug(String.format("findCustomerByName(%s, %s, %d, %d)", 
				firstName, lastName, offset, limit));
		List<Customer> customers = dao.findCustomerByName(firstName, lastName, offset, limit);
		for (Customer c: customers) {
			//hydrate each instance
			c.toString();
		}
		return new Customers(customers, offset, limit);
	}
	
	@Override
	public Customer getCustomer(int id) {
		log.debug(String.format("getCustomer(%d)", id));
		return dao.getCustomer(id);
	}

	@Override
	public void deleteCustomer(int id) {
		log.debug(String.format("deleteCustomer(%d)", id));
		Customer customer = dao.getCustomer(id);
		if (customer != null) {
			dao.deleteCustomer(customer);
		}
	}

}
