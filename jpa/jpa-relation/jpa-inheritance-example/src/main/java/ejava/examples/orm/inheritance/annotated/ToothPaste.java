package ejava.examples.orm.inheritance.annotated;

import javax.persistence.*;

/**
 * This class provides an example entity class that derives from a non-entity
 * class and accepts all mapping defaults. @see Album class for a sibling 
 * example where several of the default mappings have been overridden.
 */
@Entity 
@Table(name="ORMINH_TOOTHPASTE") //table holds this entity and parent class
public class ToothPaste extends BaseObject {
	@Access(AccessType.FIELD)
    private int size;

    @Id @GeneratedValue //id is being generated independent of other siblings
    public long getId() { return super.getId(); }
    protected void setId(long id) {
        super.setId(id);
    }
    
    public int getSize() { return size; }
    public void setSize(int size) {
        this.size = size;
    }

    @Transient
    public String getName() { return "" + size + "oz toothpaste"; }
    
    public String toString() {
        StringBuilder text = new StringBuilder(super.toString());
        return text.toString();
    }
}
