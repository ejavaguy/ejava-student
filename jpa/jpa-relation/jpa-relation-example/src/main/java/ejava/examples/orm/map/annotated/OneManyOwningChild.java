package ejava.examples.orm.map.annotated;


import javax.persistence.*;

/**
 * This class represents the child in a One-to_Many, uni-directional and
 * bi-directional relationship. The child owns the relationship in both cases.
 */
@Entity @Table(name="ORMMAP_ONEMANY_OWNCHILD")
public class OneManyOwningChild {
    @Id
    private String name;
    @ManyToOne
    private OneManyInverseParent oneInverseParent; 	
    
    protected OneManyOwningChild() {}
    public OneManyOwningChild(String name) { this.name = name; }

    public String getName() { return name; }

    public OneManyInverseParent getOneInverseParent() { return oneInverseParent; }
    public void setOneInverseParent(OneManyInverseParent oneInverseParent) {
            this.oneInverseParent = oneInverseParent;
    }
    
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append(getClass().getName());
        text.append(", name=" + name);
        if (oneInverseParent != null) {
                text.append(", oneInverseParent={" + oneInverseParent + "}");
        }
        return text.toString();
    }
}
