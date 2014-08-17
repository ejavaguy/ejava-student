package ejava.jpa.hibernatemigration.annotated;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * This class provides an example base class that uses annotated entity mapping. It uses 
 * a database generated Id.
 */
@MappedSuperclass
public class Person {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
	@Basic(optional=false)
	@Column(length=32)
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
