package ejava.examples.ejbwar.inventory.ejb;

import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import ejava.examples.ejbwar.inventory.bo.Categories;
import ejava.examples.ejbwar.inventory.bo.Category;
import ejava.examples.ejbwar.inventory.bo.Product;
import ejava.examples.ejbwar.inventory.bo.Products;
import ejava.examples.ejbwar.inventory.dao.InventoryDAO;

/**
 * This class implements the core transactional business logic for the 
 * inventory management. It is implemented as a no interface @Stateless
 * session bean and has JAX-RS and RMI facades that deal with technology-
 * specific communications with remote clients.
 */
@Stateless
public class InventoryMgmtEJB {
	@Inject
	private InventoryDAO dao;
	
	/**
	 * Returns a list of categories that match the name provided
	 * @param name
	 * @return
	 */
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Categories findCategoryByName(String name, int offset, int limit) {
		List<Category> categories = dao.findCategoryByName("%" + name + "%", offset, limit);
		for (Category category : categories) {
			category.setProductCount(categories.size());
			dao.detachCategory(category); //detach before manipulating collection 
			category.setProducts(null);
		}
		return new Categories(categories, 0, 0);
	}
	

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Category createOrGetCategory(String name) {
		List<Category> categories = dao.findCategoryByName(name, 0, 1); 
		if (categories.size()==0) {
			Category category=new Category(name);
			dao.createCategory(category);
			return category;
		}
		else {
			return categories.get(0);
		}
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Category getCategory(int id) {
		return dao.getCategory(id);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void deleteCategory(int id) {
		Category category = dao.getCategory(id);
		if (category != null) {
			dao.deleteCategory(category);
		}
	}


	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Product addProduct(Product product, String categoryName) {
	    product.setProtectedValue(UUID.randomUUID().toString());
		dao.addProduct(product);
		Category category = createOrGetCategory(categoryName);
		category.getProducts().add(product);
		return product;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Product updateProduct(Product product) {
		return dao.updateProduct(product);
	}
	
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Products findProductByName(String name, int offset, int limit) {
		return new Products(dao.findProductsByName("%" + name + "%", offset, limit), offset, limit);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Product getProduct(int id) {
		return dao.getProduct(id);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void deleteProduct(Product product) {
		dao.deleteProduct(product);
	}
}
