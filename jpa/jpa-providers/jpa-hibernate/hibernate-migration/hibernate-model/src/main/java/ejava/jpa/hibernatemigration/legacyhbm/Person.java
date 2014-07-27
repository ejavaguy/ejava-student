package ejava.jpa.hibernatemigration.legacyhbm;

/**
 * This class provides an example legacy base class that will use an external entity mapping. It uses 
 * a database generated Id.
 */
public class Person {
    private int id;
    private String name;
    
    public Person() {}
    public Person(int id) {
    	this.id = id;
    }
	
    public int getId() { return id; }
	
	public String getName() { return name; }
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public int hashCode() {
		return (name==null?0:name.hashCode());
	}
	@Override
	public boolean equals(Object obj) {
		try {
			Person rhs = (Person)obj;
			return name.equals(rhs.name);
		} catch (Exception ex) { return false; }
	}
	
	@Override
	public String toString() {
		return "id=" + id + ", name=" + name;
	}
}
