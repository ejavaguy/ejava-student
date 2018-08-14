package ejava.examples.blpurchase.blimpl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.blpurchase.bl.Catalog;
import ejava.examples.blpurchase.bo.Account;
import ejava.examples.blpurchase.bo.Cart;
import ejava.examples.blpurchase.bo.Product;

public class CatalogImpl implements Catalog {
	private static final Logger logger = LoggerFactory.getLogger(CatalogImpl.class);
	private EntityManager em;

	public void setEntityManager(EntityManager entityManager) {
		em = entityManager;
	}

	@Override
	public List<Product> getProducts(int offset, int limit) {
		TypedQuery<Product> query = em.createNamedQuery(Product.GET_PRODUCTS_QUERY, Product.class);
		if (offset > 0) {
			query.setFirstResult(offset);
		}
		if (limit > 0) {
			query.setMaxResults(limit);
		}
		return query.getResultList();
	}

	@Override
	public int addToCart(int id, String email) {
		Product product = em.find(Product.class, id);
		if (product == null) {
			logger.warn("product not found: {}", id);
			return 0;
		}
		if (product.getCount()-1 < 0) {
			logger.warn("no product left");
			return 0;
		}
		product.setCount(product.getCount()-1);

		Cart cart = em.find(Cart.class, email);
		if (cart == null) {
			List<Account> accounts = em.createNamedQuery(
					Account.FIND_BY_EMAIL_QUERY, Account.class)
					.setParameter("email", email)
					.getResultList();
			if (accounts.size() == 0) {
				logger.warn("no account found for: {}", email);
				return 0;
			}
			cart = new Cart(accounts.get(0));
			em.persist(cart);
		}
		cart.getProducts().add(product);
		
		return cart.getProducts().size();
	}
}