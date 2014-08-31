package ejava.examples.ejbwar.customer;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ejava.examples.ejbwar.customer.Customers;

/**
 * This class defines producers for resources required within the customer
 * application.
 */
public class CustomerResources {
	@PersistenceContext(unitName="webejb-customer") @Customers
	@Produces
	public EntityManager customerEM;
}
