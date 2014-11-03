package ejava.examples.ejbwar.inventory.rmi;

import javax.ejb.Remote;

import ejava.examples.ejbwar.inventory.client.InventoryClient;

/**
 * This interface defines the RMI interface to the Inventory service
 * deployed on the server. We are choosing to extend the same interface
 * the HTTP Client used since it was technology-agnostic and could be
 * directly implemented by RMI.
 */
@Remote
public interface InventoryMgmtRemote extends InventoryClient {
}
