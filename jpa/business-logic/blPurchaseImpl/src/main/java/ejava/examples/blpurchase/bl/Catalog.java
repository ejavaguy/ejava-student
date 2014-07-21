package ejava.examples.blpurchase.bl;

import java.util.List;

import ejava.examples.blpurchase.bo.Product;

/**
 * The catalog maintains a view of the inventory known to our application. 
 */
public interface Catalog {

	/**
	 * Returns a list of products in the catalog chunked into page sizes.
	 * @param offset
	 * @param limit
	 */
	List<Product> getProducts(int offset, int limit);

	/**
	 * Adds the selected product to the users' shopping cart and returns
	 * the count of items.
	 * @param id
	 * @param validEmail
	 */
	int addToCart(int id, String validEmail);
}
