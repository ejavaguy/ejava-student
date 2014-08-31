package ejava.examples.ejbwar.inventory.rmi;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
	private static final Log log = LogFactory.getLog(InventoryMgmtRMIEJB.class);
	
	@Inject
	private InventoryMgmtEJB ejb;

	@Override
	public Categories findCategoryByName(String name, int offset, int limit) {
		log.debug(String.format("findCategoryByName(%s)", name));
		return ejb.findCategoryByName(name, offset, limit);
	}

	@Override
	public boolean deleteCategory(int id) {
		log.debug(String.format("deleteCategory(%d)", id));
		ejb.deleteCategory(id);
		return true;
	}

	@Override
	public Products findProductsByName(String name, int offset, int limit) {
		log.debug(String.format("findProductByName(%s)", name));
		return ejb.findProductByName(name, offset, limit);
	}

	@Override
	public boolean deleteProduct(int id) {
		log.debug(String.format("deleteProduct(%d)", id));
		Product p = ejb.getProduct(id);
		if (p!=null) {
			ejb.deleteProduct(p);
		}
		return true;
	}

	@Override
	public Product createProduct(Product product, String category) {
		log.debug(String.format("createProduct(%s)", product));
		return ejb.addProduct(product, category);
	}

	@Override
	public Product getProduct(int id) {
		log.debug(String.format("getProduct(%d)", id));
		return ejb.getProduct(id);
	}

	@Override
	public Category getCategory(int id) {
		log.debug(String.format("getCategory(%d)", id));
		Category category = ejb.getCategory(id);
		//hydrate the object before returning
		category.toString();
		return category;
	}

	@Override
	public Product updateProduct(Product product) {
		log.debug(String.format("updateProduct(%s)", product));
		return ejb.updateProduct(product);
	}
}
