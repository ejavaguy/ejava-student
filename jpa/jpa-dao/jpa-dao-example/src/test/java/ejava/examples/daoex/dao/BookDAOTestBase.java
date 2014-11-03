package ejava.examples.daoex.dao;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Random;

import org.junit.Test;

import ejava.examples.daoex.bo.Book;

/**
 * This class defines the core tests for our Book DAOs. Sub-classes will provide
 * an implementation.
 */
public abstract class BookDAOTestBase {
	protected BookDAO dao;

	/**
	 * This helper method will create a book instance with random data
	 */
	protected Book makeBook() {
		Random random = new Random();
		Book book = new Book();
		book.setTitle("GWW-" + random.nextInt());
		book.setDescription("this and that-" + random.nextInt());
		book.setPages(1037 + random.nextInt());
		return book;
	}

	/**
	 * This method verifies our ability to create/persist a book.
	 */
	@Test
	public void testCreate() {
		Book book = makeBook();
		assertEquals("id not assigned", 0, book.getId());
		book = dao.create(book);
		assertTrue("id not assigned", book.getId()>0);
	}
	
	/**
	 * This method verifies our ability to get a book by Id
	 */
	@Test
	public void testGet() {
		//store off a book
		Book book1 = makeBook();
		book1 = dao.create(book1);
		
		//get it back by id
		Book book2 = dao.get(book1.getId());
		assertNotNull("book not found", book2);
		assertEquals("unexpected title", book1.getTitle(), book2.getTitle());
	}
	
	/**
	 * This method verifies our ability to updated an already persisted book.
	 */
	@Test
	public void testUpdate() {
		//store off a book with an original version of title
		Book book1 = makeBook();
		book1.setTitle("original");
		book1 = dao.create(book1);
		assertEquals("unexpected title", "original", book1.getTitle());
		
		//get it back by id
		Book book2 = dao.get(book1.getId());
		assertNotNull("book not found", book2);
		assertEquals("unexpected title", book1.getTitle(), book2.getTitle());

		//change the title
		book2.setTitle("changed");
		book2=dao.update(book2);

		//get it back by id and verify title changed
		Book book3 = dao.get(book1.getId());
		assertNotNull("book not found", book3);
		assertEquals("unexpected title", book2.getTitle(), book3.getTitle());
	}
	
	/**
	 * This test verifies our ability to delete a book.
	 */
	@Test
	public void testDelete() {
		//store off a book
		Book book1 = makeBook();
		book1 = dao.create(book1);
		
		//get it back by id
		Book book2 = dao.get(book1.getId());
		assertNotNull("book not found", book2);

		//delete it
		dao.remove(book1);
		
		//verify we can no longer find it
		book2 = dao.get(book1.getId());
		assertNull("book not deleted", book2);
	}
	
	/**
	 * This test verifies the ability to get a page of books from DB
	 */
	@Test
	public void testFindAll() {
		int pageSize=3;
		for (int i=0; i<pageSize*2; i++) {
			dao.create(makeBook());
		}
		List<Book> books = dao.findAll(0, pageSize);
		assertEquals("unexpected number of books", pageSize,books.size());
	}
}
