package ejava.examples.ejbwar.customer.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import ejava.examples.ejbwar.customer.Customers;
import ejava.examples.ejbwar.customer.bo.Customer;

/**
 * This class implemenets a DAO for the Customer domain.
 */
public class CustomerDAOImpl implements CustomerDAO {
	@Inject @Customers
	private EntityManager em;
	
	@Override
	public void createCustomer(Customer customer) {
		em.persist(customer);		
	}
	
	@Override
	public List<Customer> findCustomerByName(String firstName, String lastName,
			int offset, int limit) {
		TypedQuery<Customer> query = em.createNamedQuery(Customer.FIND_BY_NAME, Customer.class)
			.setParameter("firstName", "%" + firstName + "%")
			.setParameter("lastName", "%" + lastName + "%");
		if (offset > 0) {
			query.setFirstResult(offset);
		}
		if (limit > 0) {
			query.setMaxResults(limit);
		}
		return query.getResultList();
	}
	
	@Override
	public Customer getCustomer(int id) {
		return em.find(Customer.class, id);
	}
	
	@Override
	public Customer update(Customer customer) {
		return em.merge(customer);
	}
	
	@Override
	public void deleteCustomer(Customer customer) {
		em.remove(customer);
	}
}
