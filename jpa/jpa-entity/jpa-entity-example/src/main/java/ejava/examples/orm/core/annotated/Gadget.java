package ejava.examples.orm.core.annotated;

import javax.persistence.*;

/**
 * This class demonstrates the use of IDENTITY generator strategy using
 * annotations.
 */
@Entity
@Table(name="ORMCORE_GADGET")
public class Gadget {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    private String make;    
    
    public Gadget() {}
    public Gadget(long id) { this.id = id; }
    
    public long getId() { return id; }

    public String getMake() { return make; }
    public void setMake(String make) {
        this.make = make;
    }

    @Override
    public String toString() {
        return new StringBuilder()
              .append(super.toString())	       
              .append(", id=").append(id)
              .append(", make=").append(make)
              .toString();
    }    
}
