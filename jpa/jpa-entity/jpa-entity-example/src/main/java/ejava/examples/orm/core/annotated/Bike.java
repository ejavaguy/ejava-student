package ejava.examples.orm.core.annotated;

import javax.persistence.*; //brings in JPA Annotations

/**
 * This class provides the basic annotations required to make a class usable 
 * by Java Persistence without any further mapping. They are 
 * @javax.persistence.Entity to denote the class and @javax.persistence.Id
 * to denote the primary key property. See the mapped Bike example of how this 
 * can be done through a deployment descriptor instead of annotations. 
 */
@Entity  //tells ORM that this class can be mapped 
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"make","model"}))
public class Bike {
    @Id   //tells ORM that this property provides pk simple value
    private long id;
    private String make;
    private String model;
    private int size;
    
    public Bike() {} //required non-private default ctor
    public Bike(long id) { this.id = id; }

    public long getId() { return id; }

    public String getMake() { return make; }
    public void setMake(String make) {
        this.make = make;
    }
    
    public String getModel() { return model; }
    public void setModel(String model) {
        this.model = model;
    }
    
    public int getSize() { return size; }
    public void setSize(int size) {
        this.size = size;
    }
    
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString())
		       .append(", id=").append(id)
		       .append(", make=").append(make)
			   .append(", model=").append(model)
			   .append(", size=").append(size).append("in");
		return builder.toString();
	}    
}
