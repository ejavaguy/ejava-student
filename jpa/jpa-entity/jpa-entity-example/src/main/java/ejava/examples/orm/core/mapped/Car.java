package ejava.examples.orm.core.mapped;

/**
 * This class provides an example of providing more explicit mappings between
 * the entity class and the database using orm.xml.
 */

public class Car  {    
    private long id;
    private String make;
    private String model;
    private int year;
    private double cost;
    
    public Car() {}
    public Car(long id) { this.id = id; }
    
    public long getId() {
        return id;
    }

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    
    public String getModel() { return model; }
    public void setModel(String model) {
        this.model = model;
    }
    
    public int getYear() { return year; }
    public void setYear(int year) {
        this.year = year;
    }    
    
    public double getCost() { return cost; }
    public void setCost(double cost) {
        this.cost = cost;
    }    
    
    public String toString() {
        return super.toString()
            + ", id=" + id
            + ", make=" + make
            + ", model=" + model
            + ", year=" + year
            + "$" + cost;        
    }
}
