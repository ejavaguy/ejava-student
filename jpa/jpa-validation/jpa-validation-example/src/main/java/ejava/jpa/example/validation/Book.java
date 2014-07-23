package ejava.jpa.example.validation;

/**
 * This class provides an example of using an XML descriptor to define
 * constraints
 */
public class Book {
	private int id;
	
	//@NotNull(message="title is required")
	//@Size(max=32, message="title too long")
	private String title;
	//@Min(value=1, message="pages are required")
	private int pages;
	
	public int getId() { return id; }

	public String getTitle() { return title; }
	public Book setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public int getPages() { return pages; }
	public Book setPages(int pages) {
		this.pages = pages;
		return this;
	}
	
	@Override
	public String toString() {
		return title + " " + pages + "pp";
	}
}
