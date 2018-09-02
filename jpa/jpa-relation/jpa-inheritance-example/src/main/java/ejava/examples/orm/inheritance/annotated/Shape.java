package ejava.examples.orm.inheritance.annotated;

import javax.persistence.*;

/**
 * This class provides an example of mixing two inheritance stratgies; 
 * non-entity inheritance and join. The parent of this class is a non-entity
 * and will be stored inside this table. Since this class has also defined
 * the inheritance stratgy to be JOIN, all sub-classes will have their own 
 * tables and join with this table to form an object.
 *
 */
@Entity @Table(name="ORMINH_SHAPE")
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class Shape extends BaseObject {
    private int posx;
    private int posy;
    
    @Id @GeneratedValue
    public long getId()           { return super.getId(); }
    protected void setId(long id) { super.setId(id); }
    
    public int getPosx() {
        return posx;
    }
    public void setPosx(int posx) {
        this.posx = posx;
    }
    public int getPosy() {
        return posy;
    }
    public void setPosy(int posy) {
        this.posy = posy;
    }

    public String toString() {
        StringBuilder text = new StringBuilder(super.toString());
        text.append(", posx=" + posx);
        text.append(", posy=" + posy);
        return text.toString();
    }
}
