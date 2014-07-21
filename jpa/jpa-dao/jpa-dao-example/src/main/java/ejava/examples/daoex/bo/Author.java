package ejava.examples.daoex.bo;

import java.util.LinkedList;
import java.util.List;

public class Author {
	private int id;
	private String firstName;
	private String lastName;
	private List<Book> books=new LinkedList<Book>();
	
	public Author() {}
	public Author(int id) { this.id=id; }
	
	public int getId() { return id; }

	public String getFirstName() { return firstName; }
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() { return lastName; }
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public List<Book> getBooks() { return books; }
	public void setBooks(List<Book> books) {
		this.books = books;
	}
}
