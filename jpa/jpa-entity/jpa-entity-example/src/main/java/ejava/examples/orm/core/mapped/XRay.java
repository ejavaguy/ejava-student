package ejava.examples.orm.core.mapped;

/**
 * This class provides an example of embedding another object within a 
 * containing object being mapped to the database. In this case, XRay is
 * assigned a primary key and mapped to the database. It has two local 
 * properties, but maker name, address, and phone are part of a Manufacturer
 * class. This works much like the embedded-id case, except the embedded class
 * is just used for normal properties and not primary key values.
 */
public class XRay {
    private long id;
    private Manufacturer maker;
    private String model;
    
    public XRay() {}
    public XRay(long id) { this.id = id; }
    
    public long getId() { return id; }

    public String getModel() { return model; }
    public void setModel(String model) {
        this.model = model;
    }    

    public ejava.examples.orm.core.mapped.Manufacturer getMaker() { return maker; }
    public void setMaker(ejava.examples.orm.core.mapped.Manufacturer maker) {
        this.maker = maker;
    }
    
    public String toString() {
        return super.getClass().getName() +
            ", id=" + id +
            ", model=" + model +
            ", maker=" + maker;
    }
}
