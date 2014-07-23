package ejava.examples.orm.core.mapped;

import java.io.Serializable;

import javax.persistence.*;

/**
 * This class provides an example of a primary key class that can be 
 * embedded into the referenced class. The containing class will simply
 * use an instance of this class rather than having separate fields that
 * match the fields of this class. All fields will be mapped by the 
 * containing class.
 */
@Embeddable
public class MakeModelPK implements Serializable {
    private static final long serialVersionUID = 1L;
    private String make;
    private String model;
    
    public MakeModelPK() {}
    public MakeModelPK(String make, String model) {
        this.make = make;
        this.model = model;
    }
    
    public String getMake() { return make; }
    public String getModel() { return model; }

    public int hashCode() { return make.hashCode() + model.hashCode(); }
    public boolean equals(Object obj) {
        try {
            if (this == obj) return true;
            return make.equals(((MakeModelPK)obj).getMake()) &&
                   model.equals(((MakeModelPK)obj).getModel());
            
        } catch (Throwable ignored) { //catch NP & Cast Exceptions 
            return false;
        }
    }
    
    public String toString() {
        return super.toString() +
            ", make=" + make +
            ", model=" + model;
    }
}
