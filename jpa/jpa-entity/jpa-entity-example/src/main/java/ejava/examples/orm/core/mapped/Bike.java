package ejava.examples.orm.core.mapped;

/**
 * This class provides a pure POJO class that is mapped by BIKE-orm.xml
 * into the database. See the annotated Bike example for how this can be done 
 * through class annotations.
 */
public class Bike {
    private long id; //orm.xml file will map this field to Identity
    private String make;
    private String model;
    private int size;
    
    public Bike() {}
    public Bike(long id) { this.id = id; }

    public long getId() {
        return id;
    }

    public String getMake() { return make; }
    public void setMake(String make) {
        this.make = make;
    }
    
    public String getModel() { return model; }
    public void setModel(String model) {
        this.model = model;
    }
    
    public int getSize() { return size;}
    public void setSize(int size) {
        this.size = size;
    }
    
    public String toString() {
        return super.toString() + " id=" + id + 
            ", make=" + make + 
            ", model=" + model + 
            ", " + size + "in";
    }
}
