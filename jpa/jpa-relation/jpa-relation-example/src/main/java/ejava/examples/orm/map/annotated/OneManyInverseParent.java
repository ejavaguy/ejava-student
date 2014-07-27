package ejava.examples.orm.map.annotated;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.*;

/**
 * This class represents a parent in One-to-Many, bi-directional relationship.
 * The parent is the inverse side of the relationship and keeps the children 
 * in a Map. The OneManyOwningChild defines the relationship.
 */
@Entity @Table(name="ORMMAP_ONEMANY_INVPARENT")
public class OneManyInverseParent {
    @Id
    private String name;

    @OneToMany(mappedBy="oneInverseParent")
    @MapKey(name="name")
    private Map<String, OneManyOwningChild> ownedByChildren =
            new HashMap<String, OneManyOwningChild>();

    protected OneManyInverseParent() {}
    public OneManyInverseParent(String name) { this.name = name; }

    public String getName() { return name; }
    
    public Map<String, OneManyOwningChild> getOwnedByChildren() { return ownedByChildren; }
    public void setOwnedByChildren(
                    Map<String, OneManyOwningChild> ownedByChildren) {
        this.ownedByChildren = ownedByChildren;
    }

    public String toString() {
        StringBuilder text = new StringBuilder();
        
        text.append(getClass().getName());
        text.append(", name=" + name);
        if (ownedByChildren != null) {
            text.append(", ownedByChildren=(" + ownedByChildren.size() + ")={");
            for (String key : ownedByChildren.keySet()) {
                    text.append(ownedByChildren.get(key).getName() + ", ");
            }
            text.append("}");
        }
        return text.toString();
    }
}
