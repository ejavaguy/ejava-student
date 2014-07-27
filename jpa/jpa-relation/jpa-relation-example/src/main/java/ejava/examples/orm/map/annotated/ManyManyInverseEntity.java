package ejava.examples.orm.map.annotated;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.*;

/**
 * This class represents a the inverse side of a Many-to-Many bi-directional 
 * relationship stored as a Map. The ManyManyOwningEntity owns the
 * relationship. 
 */
@Entity @Table(name="ORMMAP_MANYMANY_INVENTITY")
public class ManyManyInverseEntity {
    @Id
    private String name;

    @ManyToMany(mappedBy="ownedInverseEntities")
    @MapKey(name="name")
    private Map<String, ManyManyOwningEntity> ownedByEntities = new HashMap<String, ManyManyOwningEntity>();
    
    
    protected ManyManyInverseEntity() {}
    public ManyManyInverseEntity(String name) { this.name = name; }

    public String getName() { return name; }

    public Map<String, ManyManyOwningEntity> getOwnedByEntities() { return ownedByEntities; }
    public void setOwnedByEntities(Map<String, ManyManyOwningEntity> ownedByEntities) {
        this.ownedByEntities = ownedByEntities;
    }
    
    public String toString() {
        StringBuilder text = new StringBuilder();
        
        text.append(getClass().getName());
        text.append(", name=" + name);
        if (ownedByEntities != null) {
            text.append(", ownedByEntities=(" + ownedByEntities.size() + ")={");
            for (String key : ownedByEntities.keySet()) {
                text.append(ownedByEntities.get(key).getName() + ", ");
            }
            text.append("}");
        }
        return text.toString();
    }
}
