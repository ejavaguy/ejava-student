package ejava.examples.orm.rel.annotated;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

/**
 * This class provides an example of one end of a ManyToMany relationship. 
 * This class will "own" the relationship, thus define the join table.
 */
@Entity @Table(name="ORMREL_AUTHOR")
public class Author {
    @Id @GeneratedValue
    private long id;
    private String name;

    @ManyToMany
    @JoinTable(name="ORMREL_AUTHOR_MEDIA", //defines the link table
                //defines the column in the link table for author FK
            joinColumns={@JoinColumn(name="LINK_AUTHOR_ID")},
                //defines the column in the link table for the media FK
            inverseJoinColumns={@JoinColumn(name="LINK_MEDIA_ID")})
    @OrderBy("title DESC") //order the list returned from database
    private List<Media> media = new ArrayList<Media>();
    
    public Author()        {}
    public Author(long id) { this.id = id; }
    
    public long getId() { return id; }    
    
    public List<Media> getMedia() { return media; }
    public void setMedia(List<Media> media) {
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
        for (Media m: media) {
            text.append(m.getId() + ", ");
        }
        text.append("}");
        return text.toString();
    }
}
