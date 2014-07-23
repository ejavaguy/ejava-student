package ejava.examples.orm.core.mapped;

/**
 * This class provides an example of using a TABLE GeneratedValue schema. The
 * definition of the primary key generation is supplied in the orm.xml file.
 */
public class EggBeater {
    private long id;
    private String make;    
    
    public EggBeater() {}
    public EggBeater(long id) { this.id = id; }
    
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
