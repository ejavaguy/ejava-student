package ejava.examples.ejbwar.inventory.client;

import javax.ws.rs.client.ResponseProcessingException;

import ejava.examples.ejbwar.inventory.bo.Categories;
import ejava.examples.ejbwar.inventory.bo.Category;
import ejava.examples.ejbwar.inventory.bo.Product;
import ejava.examples.ejbwar.inventory.bo.Products;

/**
 * Defines a remote interface used by clients of the WAR-deployed EJB.
 */
public interface InventoryClient {
	Categories findCategoryByName(String name, int offset, int limit) throws ResponseProcessingException;
	Category getCategory(int id) throws ResponseProcessingException;
	boolean deleteCategory(int id) throws ResponseProcessingException;
	
	Product createProduct(Product product, String string) throws ResponseProcessingException;
	Products findProductsByName(String name, int offset, int limit) throws ResponseProcessingException;
	Product getProduct(int id) throws ResponseProcessingException;
	Product updateProduct(Product product) throws ResponseProcessingException;
	boolean deleteProduct(int id) throws ResponseProcessingException;
}
