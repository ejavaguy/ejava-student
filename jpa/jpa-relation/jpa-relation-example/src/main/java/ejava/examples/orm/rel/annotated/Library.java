package ejava.examples.orm.rel.annotated;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.*;

/** 
 * This class provides an example of using the java.util.Map with 
 * relationships.
 */
@Entity @Table(name="ORMREL_LIBRARY")
public class Library {
    @Id @GeneratedValue
    private long id;
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @MapKey(name="id")
    private Map<Long, Borrower> borrowers = new HashMap<Long, Borrower>();
    
    public Library() {}
    public Library(long id) { this.id = id; }

    public long getId() { return id; }
    
    public Map<Long, Borrower> getBorrowers() { return borrowers; }
    public void setBorrowers(Map<Long, Borrower> borrowers) {
        this.borrowers = borrowers;
    }

    private String myInstance() {
        String s=super.toString();
        s = s.substring(s.lastIndexOf('.')+1);
        return s;
    }

    public String toString() {
        StringBuilder text = new StringBuilder(myInstance());
        text.append(", id=" + id);
        text.append(", borrowers(" + borrowers.size() + ")={");
        for(long bid: borrowers.keySet()) {
            text.append("id=" + bid + "{" +
                    borrowers.get(bid).getName() +
                    "}, ");
        }
        text.append("}");
        return text.toString();
    }

}
