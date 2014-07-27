package ejava.examples.orm.map.annotated;

import javax.persistence.*;

/**
 * This class represents a stand-alone object in a Many-to-Many, 
 * uni-directional relationship. This entity makes no 
 * reference to the other entities. ManyManyOwningEntity defines the 
 * relationship.
 */
@Entity @Table(name="ORMMAP_MANYMANY_ENTITY")
public class ManyManyEntity {
    @Id
    private String name;

    protected ManyManyEntity() {}
    public ManyManyEntity(String name) {
            this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) {
            this.name = name;
    }
    
    public String toString() {
            StringBuilder text = new StringBuilder();
            text.append(getClass().getName());
            text.append(", name=" + name);
            return text.toString();
    }
}
