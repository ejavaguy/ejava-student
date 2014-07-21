package ejava.examples.daoex.bo;

/**
 * This is an example entity class that will get mapped into the database 
 * using a DAO. Note that the id carries the primary key and should not be 
 * modified after the object is created. This is why setId() is set to private.
 */
public class Book  {
    private long id;
    private String title;
    private int pages;
    private String description;
    
    public Book() {}
    public Book(long id) { this.id = id; }
    
    public long getId() { return id; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getPages() { return pages; }
    public void setPages(int pages) {
        this.pages = pages;
    }
    
    public String getTitle() { return title; }
    public void setTitle(String title) {
        this.title = title;
    }

    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("id=" + id);
        text.append(", title=").append(title);
        text.append(", pages=").append(pages);
        return text.toString();
    }
}
