package ejava.examples.orm.core.mapped;

/**
 * This class demonstrates the use of SEQUENCE generator strategy using
 * descriptors.
 */
public class Fan {
    private long id;
    private String make;    
    
    public Fan() {}
    public Fan(long id) { this.id = id; }
    
    public long getId() { return id; }

    public String getMake() { return make; }
    public void setMake(String make) {
        this.make = make;
    }

    public String toString() {
        return super.toString()
            + ", id=" + id
            + ", make=" + make;        
    }
}
