package ejava.examples.ejbwar.inventory.dao;

import java.util.List;

import ejava.examples.ejbwar.inventory.bo.Category;
import ejava.examples.ejbwar.inventory.bo.Product;

/**
 * Defines the interface for the CRUD inventory methods.
 */
public interface InventoryDAO {

	void createCategory(Category category);
	Category getCategory(int id);
	List<Category> findCategoryByName(String criteria, int offset, int limit);
	void deleteCategory(Category category);
	void detachCategory(Category category);

	void addProduct(Product p);
	Product getProduct(int id);
	Product updateProduct(Product product);
	List<Product> findProductsByName(String criteria, int offset, int limit);
	void deleteProduct(Product p);
}