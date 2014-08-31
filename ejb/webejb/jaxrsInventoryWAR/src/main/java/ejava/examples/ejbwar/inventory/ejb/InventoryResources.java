package ejava.examples.ejbwar.inventory.ejb;

import javax.enterprise.inject.Produces;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ejava.examples.ejbwar.inventory.cdi.Inventory;

/**
 * This class is used to provide resources required by the application.
 * We must have a Web-INF/beans.xml file in place in the WAR to enable CDI.
 */
public class InventoryResources {
	@Produces @Inventory
	@PersistenceContext(unitName="webejb-inventory")
	public EntityManager inventoryEM;
}
