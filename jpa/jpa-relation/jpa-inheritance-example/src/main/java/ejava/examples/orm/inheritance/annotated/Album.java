package ejava.examples.orm.inheritance.annotated;

import javax.persistence.*;

/**
 * This class provides an example of inheriting from a non-entity base class.
 * In this case, the base class is mapped into the Album table. We physically
 * map the parent properties into table colums here. 
 * @see Toothpaste class for a sibling example that will accept the parent
 * mapping defaults.
 */
@Entity 
@Table(name="ORMINH_ALBUM") //this table holds both this entity and parent class
@AttributeOverrides({
    @AttributeOverride(name="version", column=@Column(name="ALBUM_VERSION"))
})
public class Album extends BaseObject {
	@Access(AccessType.FIELD)
    private String artist;
	@Access(AccessType.FIELD)
    private String title;

    @Id @GeneratedValue //id is being generated independent of other siblings
    @Column(name="ALBUM_ID") 
    public long getId() { return super.getId(); }
    protected void setId(long id) {
        super.setId(id);
    }
    
    public String getArtist() { return artist; }
    public void setArtist(String artist) {
        this.artist = artist;
    }
    
    public String getTitle() { return title; }
    public void setTitle(String title) {
        this.title = title;
    }

    @Transient
    public String getName() { return artist + ":" + title; }
    
    public String toString() {
        StringBuilder text = new StringBuilder(super.toString());
        return text.toString();
    }
}
