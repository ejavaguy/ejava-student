package ejava.examples.orm.core.mapped;

/**
 * This class an example of what to do with extra getter/setter fields
 * (or fields) that should not be considered part of the persistence.
 * The getMakeModel() convenience method will cause processing to fail 
 * because there is no matching setter(). Marking it with Transient in
 * the orm.xml file fixes this.
 */
public class Tank {
    private long id;
    private String make;
    private String model;
    
    public Tank() {}
    public Tank(long id) { this.id = id; }
    
    public long getId() { return id; }
    protected void setId(long id) {
        this.id = id;
    }

    public String getMakeModel() { return make + " " + model; }    

    public String getMake() { return make; }
    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() { return model; }
    public void setModel(String model) {
        this.model = model;
    }

    public String toString() {
        return super.toString() +
           ", make=" + make +
           ", model=" + model;
    }
}
