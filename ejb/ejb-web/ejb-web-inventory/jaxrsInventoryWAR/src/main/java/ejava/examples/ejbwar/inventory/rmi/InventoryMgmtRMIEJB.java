package ejava.examples.ejbwar.inventory.rmi;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.ejbwar.inventory.bo.Categories;
import ejava.examples.ejbwar.inventory.bo.Category;
import ejava.examples.ejbwar.inventory.bo.Product;
import ejava.examples.ejbwar.inventory.bo.Products;
import ejava.examples.ejbwar.inventory.ejb.InventoryMgmtEJB;

/**
 * This EJB acts as a remote facade for the InventoryEJB business logic.
 */
@Stateless
public class InventoryMgmtRMIEJB implements InventoryMgmtRemote {
	private static final Logger logger = LoggerFactory.getLogger(InventoryMgmtRMIEJB.class);
	
	@Inject
	private InventoryMgmtEJB ejb;

	@Override
	public Categories findCategoryByName(String name, int offset, int limit) {
		logger.debug("findCategoryByName({})", name);
		return ejb.findCategoryByName(name, offset, limit);
	}

	@Override
	public boolean deleteCategory(int id) {
		logger.debug("deleteCategory({})", id);
		ejb.deleteCategory(id);
		return true;
	}

	@Override
	public Products findProductsByName(String name, int offset, int limit) {
		logger.debug("findProductByName({})", name);
		return ejb.findProductByName(name, offset, limit);
	}

	@Override
	public boolean deleteProduct(int id) {
		logger.debug("deleteProduct({})", id);
		Product p = ejb.getProduct(id);
		if (p!=null) {
			ejb.deleteProduct(p);
		}
		return true;
	}

	@Override
	public Product createProduct(Product product, String category) {
		logger.debug("createProduct({})", product);
		return ejb.addProduct(product, category);
	}

	@Override
	public Product getProduct(int id) {
		logger.debug(String.format("getProduct(%d)", id));
		return ejb.getProduct(id);
	}

	@Override
	public Category getCategory(int id) {
		logger.debug("getCategory({})", id);
		Category category = ejb.getCategory(id);
		//hydrate the object before returning
		category.getProducts().size();
		return category;
	}

	@Override
	public Product updateProduct(Product product) {
		logger.debug("updateProduct({})", product);
		return ejb.updateProduct(product);
	}
}
