package ejava.examples.orm.core.annotated;

import javax.persistence.*;

import ejava.examples.orm.core.MowerPK;

/**
 * This class provides an example of expressing an IdClass for a compound 
 * primary key using annotations. The primary key class does not use 
 * annotations. All annotations are within the using class.
 */
@Entity
@Table(name="ORMCORE_MOWER")
@IdClass(MowerPK.class)
public class Mower {
    @Id
    private String make;
    @Id
    private String model;    
    private int size;
    
    public Mower() {}
    public Mower(String make, String model) {
        this.make = make;
        this.model = model;
    }
    
    public String getMake() { return make; }    
    public String getModel() { return model; }

    public int getSize() { return size; }
    public void setSize(int size) {
        this.size = size;
    }
    
    public String toString() {
        return new StringBuilder()
           .append(super.hashCode())
           .append(", make=").append(make)
           .append(", model=").append(model)
           .append(", size=").append(size)
           .toString();
    }
}
