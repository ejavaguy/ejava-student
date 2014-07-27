package myorg.relex.one2many;

import java.util.Date;

import javax.persistence.*;

/**
 * This class is an example of a non-entity class that will be mapped to a dependent table
 * and form the many side of an @ElementCollection.
 */
@Embeddable
public class Produce {
	public static enum Color { RED, GREEN, YELLOW }
	@Column(length=16)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(length=10)
    private Color color;
    
    @Temporal(TemporalType.DATE)
    private Date expires;
    
    public Produce() {}
	public Produce(String name, Color color, Date expires) {
		this.name = name;
		this.color = color;
		this.expires = expires;
	}

	public String getName() { return name; }
	public Color getColor() { return color; }
	public Date getExpires() { return expires; }
}
