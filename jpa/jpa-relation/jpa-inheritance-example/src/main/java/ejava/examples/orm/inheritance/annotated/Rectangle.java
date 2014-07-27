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
@Entity @Table(name="ORMINH_RECTANGLE")
public class Rectangle extends Shape {
    private int height;
    private int width;

    @Transient
    public String getName() {
        return "rectangle:" + getId();
    }

    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }

    public String toString() {
        StringBuilder text = new StringBuilder(super.toString());
        text.append(", height=" + height);
        text.append(", width=" + width);
        return text.toString();
    }
}
