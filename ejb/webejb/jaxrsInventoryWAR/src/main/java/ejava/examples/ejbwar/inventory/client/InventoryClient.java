package ejava.examples.ejbwar.inventory.client;

import ejava.examples.ejbwar.inventory.bo.Categories;
import ejava.examples.ejbwar.inventory.bo.Category;
import ejava.examples.ejbwar.inventory.bo.Product;
import ejava.examples.ejbwar.inventory.bo.Products;

/**
 * Defines a remote interface used by clients of the WAR-deployed EJB.
 */
public interface InventoryClient {
	Categories findCategoryByName(String name, int offset, int limit) throws Exception;
	Category getCategory(int id) throws Exception;
	boolean deleteCategory(int id) throws Exception;
	
	Product createProduct(Product product, String string) throws Exception;
	Products findProductsByName(String name, int offset, int limit) throws Exception;
	Product getProduct(int id) throws Exception;
	Product updateProduct(Product product) throws Exception;
	boolean deleteProduct(int id) throws Exception;
}
