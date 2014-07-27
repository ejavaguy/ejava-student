package ejava.examples.orm.rel.annotated;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.*;

/**
 * This class is used as an example of a @OneToMay, using a join table. In
 * this example, the Inventory has a OneToMany relationship with Media. The
 * navigation is uni-directional from Inventory to Media and the MEDIA table
 * has no foreign key to reference the INVENTORY table. An INVENTORY_MEDIA
 * join (or link) table is used to make the linkage.
 */
@Entity @Table(name="ORMREL_INVENTORY")
public class Inventory {
    @Id @GeneratedValue
    private long id;
    private String name;

    @OneToMany(cascade={CascadeType.ALL})
    @JoinTable(name="ORMREL_INVENTORY_MEDIA")
    //@JoinColumn(name="INVENTORY_ID")
    private Collection<Media> media = new ArrayList<Media>();
    
    public Inventory()        {}
    public Inventory(long id) { this.id = id; }
    
    public long getId() { return id; }

    public Collection<Media> getMedia() { return media; }
    public void setMedia(Collection<Media> media) {
        this.media = media;
    }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
    }
    
    public String toString() {
        StringBuilder text = new StringBuilder(super.toString());
        text.append(", id=" + id);
        text.append(", name=" + name);
        text.append(", media(" + media.size() + ")={");
        for(Media m: media) {
            text.append(m.getId() + ",");
        }
        text.append("}");
        return text.toString();
    }
}
