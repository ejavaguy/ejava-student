package ejava.examples.orm.inheritance.annotated;

import javax.persistence.*;

/**
 * This class provides an attempt to re-define the JOIN InheritanceType of the
 * parent class to a TABLE_PER_CLASS type. If you look closly at the database,
 * this instruction is ignored and the Cube sub-table is JOINED with the parent
 * class tables to form the object instead.
 *
 */
@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS) //ignored!!!
@Table(name="ORMINH_CUBE")
public class Cube extends Rectangle {
    private int depth;

    public int getDepth() {
        return depth;
    }
    public void setDepth(int depth) {
        this.depth = depth;
    }
    
    public String toString() {
        StringBuilder text = new StringBuilder(super.toString());
        text.append(", depth=" + depth);
        return text.toString();
    }
    
}
