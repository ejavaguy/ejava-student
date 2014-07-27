package ejava.examples.orm.map.annotated;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.*;

/**
 * This class represents a parent in a One-to-Many, uni-directional
 * relationship, relative to the parent. The OneManyChild
 * makes no reference to this relationship. Of note, there is no such
 * thing as a One-to-Many, bi-directional relationship, owned by the parent 
 * in JPA. All One-to-Many bi-directional relationships must be owned by
 * the child.
 */
@Entity @Table(name="ORMMAP_ONEMANY_OWNPARENT")
public class OneManyOwningParent {
    @Id @Column(name="ID")
    private String name;

    @ManyToMany
    @MapKey(name="name")
    @JoinTable(name="ORMMAP_OWNPARENT_INVCHILD_LINK",
        joinColumns=@JoinColumn(name="PARENT_ID"),
        inverseJoinColumns=@JoinColumn(name="CHILD_ID"))
    private Map<String, OneManyChild> ownedChildren =
            new HashMap<String, OneManyChild>();
    
    protected OneManyOwningParent() {}
    public OneManyOwningParent(String name) { this.name = name; }
    
    public String getName() { return name; }
    public void setName(String name) {
            this.name = name;
    }
    
    public Map<String, OneManyChild> getOwnedChildren() { return ownedChildren; }
    public void setOwnedChildren(Map<String, OneManyChild> ownedChildren) {
            this.ownedChildren = ownedChildren;
    }
    
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append(getClass().getName());
        text.append(", name=" + name);
        if (ownedChildren != null) {
                text.append(", ownedChildren=" + ownedChildren);
        }		
        return text.toString();
    }
}
