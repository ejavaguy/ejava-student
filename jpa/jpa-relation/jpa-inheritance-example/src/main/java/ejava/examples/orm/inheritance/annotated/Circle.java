package ejava.examples.orm.inheritance.annotated;

import javax.persistence.*;

/**
 * This class provides an example of an entity sub-class that is part of a 
 * mixed strategy of inheritance. The root base class is a non-entity and 
 * the immediate base class uses a join table strategy. That means that a table
 * will be created to hold the unque properties of this class and joined with
 * the parent table.
 *
 * @author jcstaff
 */
@Entity @Table(name="ORMINH_CIRCLE")
public class Circle extends Shape {
    private int radius;

    @Transient
    public String getName() {
        return "circle:" + getId();
    }

    public int getRadius() {
        return radius;
    }
    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String toString() {
        StringBuilder text = new StringBuilder(super.toString());
        text.append(", radius=" + radius);
        return text.toString();
    }
}
