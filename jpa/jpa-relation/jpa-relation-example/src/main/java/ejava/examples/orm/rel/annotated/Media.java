package ejava.examples.orm.rel.annotated;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.*;

/**
 * This class represents a media topic, for which there are many copies and
 * authors. There are also many media in an Inventory<p/>
 * 
 * It is an example of a ManyToOne uni-directional relationship. There are
 * zero to many MediaCopy(s) for each Media. However, there is no reference to 
 * the copies from Media. All access to the copies must come through querying
 * the MediaCopies. MediaCopy happens to both "own" the relationship and 
 * be the only that it can be navigated by (uni-directional).<p/>
 * 
 * It is also an example of a OneToMany uni-directional relationship using 
 * a Join (or Link) table. There are zero to many Media in Inventory. However, 
 * there is no reference to the Inventory from this class or MEDIA table. The
 * linkage is done through a separate table defined in the Inventory class.
 * Inventory both "owns" the relationship and is the only side that it can
 * be navigated by.
 * 
 * It is also an example of ManyToMany bi-directional relationship. Each Media
 * can have zero to many Authors. Each Author can have zero to many Media.
 * Author "owns" the relationship (thus defines the join/link table). Media
 * only defines the mapping back to Author. The relationship can be navigated
 * by either side.
 */
@Entity @Table(name="ORMREL_MEDIA")
public class Media  {
    //private static Log log = LogFactory.getLog(Media.class);

    @Id @GeneratedValue @Column(name="MEDIA_ID")
    private long id;
    private String title;    
    @ManyToMany(mappedBy="media")  //names property in Author that points to us
    private Collection<Author> authors = new ArrayList<Author>();
    
    public Media() { 
        //log.debug(super.toString() + ": ctor()"); 
    }

    public long getId() { return id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) {
        this.title = title;
    }        

    public Collection<Author> getAuthors() { return authors; }
    public void setAuthors(Collection<Author> authors) {
        this.authors = authors;
    }
    
    public String toString() {
        StringBuilder text = new StringBuilder(super.toString());
        text.append(", id=" + id);
        text.append(", title=" + title);
        text.append(", authors(" + authors.size() + ")={");
        for (Author a: authors) {
            text.append(a.getId() + ", ");
        }
        text.append("}");
        return text.toString();
    }
}
