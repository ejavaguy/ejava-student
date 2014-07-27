package ejava.examples.orm.onetomany.annotated;

import javax.persistence.*;

/** 
 * This class implements the many side of a uni-directional One-to-Many
 * relationship. It knows nothing of the parent.
 */
@Entity(name="O2MChild") @Table(name="ORMO2M_CHILD")
public class OneManyChild {
    @Id @GeneratedValue @Column(name="CHILDID")
    private long id;
    private String name;
    
    
    public OneManyChild() {}
    public OneManyChild(long id) { this.id=id; }
    public OneManyChild(String name) { this.name = name; }

    
    public long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
    }
    
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("id=" + id);
        text.append(", name=" + name);
        return text.toString();
    }
}
