package ejava.examples.orm.core.annotated;

import java.io.Serializable;

import javax.persistence.*;

/**
 * This class provides an example of a primary key class that can be 
 * embedded into the referenced class. The containing class will simply
 * use an instance of this class rather than having separate fields that
 * match the fields of this class.
 */
@Embeddable
public class NapsackPK implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name="NAPSACK_MAKE") //maps field to column of containing class
    private String make;
    @Column(name="NAPSACK_MODEL")//maps field to column of containing class
    private String model;
    
    public NapsackPK() {}
    public NapsackPK(String make, String model) {
        this.make = make;
        this.model = model;
    }
    
    public String getMake() { return make; }
    public String getModel() { return model; }

    public int hashCode() { return make.hashCode() + model.hashCode(); }
    public boolean equals(Object obj) {
        try {
            if (this == obj) return true;
            return make.equals(((NapsackPK)obj).getMake()) &&
                   model.equals(((NapsackPK)obj).getModel());
            
        } catch (Throwable ignored) { //catch NP & Cast Exceptions 
            return false;
        }
    }
    
    public String toString() {
        return new StringBuilder()
            .append(super.toString())	       
            .append(", make=").append(make)
            .append(", model=").append(model)
            .toString();
    }
}
