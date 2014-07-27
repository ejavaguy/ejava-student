package ejava.examples.orm.inheritance.annotated;

import java.util.Date;

import javax.persistence.*;

/** 
 * This class provides an example of an entity sub-class that is part of a 
 * single table inheritance strategy. The table and primary key generation is 
 * being defined by the parent class. This class is accepting all defaults. 
 * @see Bead class for a sibling example of overriding the discriminator value.
 */
@Entity
public class Soup extends Product {
    public enum SoupType {
        UNKNOWN("Unknown"),
        CHICKEN_NOODLE("Chicken Noodle"), 
        NEW_ENGLAND_CLAM_CHOWDER("New England Clam Chowder"), 
        TOMATO("Tomato");
        private String text;
        private SoupType(String text) { this.text = text; }
        public String text() { return text; }
    };

    @Enumerated(EnumType.STRING)
    @Column(name="SOUPTYPE", length=16)
    private SoupType type = SoupType.UNKNOWN;
    @Temporal(TemporalType.DATE)
    private Date expiration;
    
    public Soup() {}
    public Soup(long id) { super(id); }

    public Date getExpiration() { return expiration; }
    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public SoupType getSoupType() { return type; }
    public void setSoupType(SoupType type) {
        this.type = type;
    }

    @Transient
    public String getName() { return type.text() + "Soup"; }
    
    public String toString() {
        StringBuilder text = new StringBuilder(super.toString());
        text.append(", type=" + type);
        text.append(", expiration=" + expiration);
        return text.toString();
    }
}
