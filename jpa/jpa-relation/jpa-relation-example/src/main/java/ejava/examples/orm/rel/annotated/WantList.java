package ejava.examples.orm.rel.annotated;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.*;

/**
 * This class is an example of a uni-directional ManyToMany relationship. 
 * This class both "owns" the relationship to Media and is the only direction
 * in which it can be navigated. There are many Media in a WantList and
 * many WantLists associated with a Media.
 */
@Entity @Table(name="ORMREL_WANTED")
public class WantList {
    @Id @GeneratedValue
    private long id;
    @ManyToMany
    @JoinTable(name="ORMREL_WANTED_MEDIA") //define table, but let columns use default names
    private Collection<Media> media = new ArrayList<Media>();
    
    public long getId() { return id; }
    
    public Collection<Media> getMedia() { return media; }
    public void setMedia(Collection<Media> media) {
        this.media = media;
    }
}
