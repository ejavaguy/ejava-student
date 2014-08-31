package ejava.examples.ejbwar.inventory.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import ejava.examples.ejbwar.inventory.bo.Category;
import ejava.examples.ejbwar.inventory.bo.Product;
import ejava.examples.ejbwar.inventory.cdi.Inventory;

/**
 * This DAO implementation uses JPA and an injected entity manager to 
 * perform CRUD operations on inventory data.
 */
public class InventoryDAOImpl implements InventoryDAO {
	@Inject @Inventory
	private EntityManager em;
	
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@Override
	public void createCategory(Category category) {
		em.persist(category);
	}
	
	@Override
	public Category getCategory(int id) {
		return em.find(Category.class, id);
	}
	
	@Override
	public List<Category> findCategoryByName(String criteria, int offset, int limit) {
		TypedQuery<Category> query = em.createNamedQuery(Category.FIND_BY_NAME, Category.class)
				.setParameter("criteria", criteria==null ? "%" : criteria);
		applyBounds(query, offset, limit);
		return query.getResultList();
	}
	
	@Override
	public void detachCategory(Category category) {
		em.detach(category);
	}
	
	@Override
	public void deleteCategory(Category category) {
		em.remove(category);
	}
	
	@Override
	public void addProduct(Product p) {
		em.persist(p);
	}
	
	@Override
	public Product getProduct(int id) {
		return em.find(Product.class, id);
	}
	
	@Override
	public Product updateProduct(Product product) {
		return em.merge(product);
	}
	@Override
	public List<Product> findProductsByName(String criteria, int offset, int limit) {
		TypedQuery<Product> query = em.createNamedQuery(Product.FIND_BY_NAME, Product.class)
				.setParameter("criteria", criteria==null ? "%" : criteria);
		applyBounds(query, offset, limit);
		return query.getResultList();
	}
	
	@Override
	public void deleteProduct(Product product) {
		//make sure we are dealing with a managed instance
		if (!em.contains(product)) {
			product = getProduct(product.getId());
		}
		if (product!=null) {
			for (Category category :em.createNamedQuery(Category.FIND_BY_PRODUCT, Category.class)
					.setParameter("product", product)
					.getResultList()) {
				//since product and catagory are currently managed a simple
				//object match should work here
				category.getProducts().remove(product);
			}
		em.remove(product);
		}
	}
	
	private void applyBounds(TypedQuery<?> query, int offset, int limit) {
		if (offset > 0) {
			query.setFirstResult(offset);
		}
		if (limit > 0) {
			query.setMaxResults(limit);
		}
	}
	
}
