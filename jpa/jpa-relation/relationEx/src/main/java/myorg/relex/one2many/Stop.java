package myorg.relex.one2many;

import javax.persistence.*;

/**
 * This class provides an example of the many side of a one-to-many, uni-directional relationship
 * mapped using a foreign key in the child entity table. Note that all mapping will be from the one/owning
 * side and no reference to the foreign key exists within this class.
 */
@Entity
@Table(name="RELATIONEX_STOP")
public class Stop {
    @Id @GeneratedValue
    private int id;
    @Column(length=16)
    private String name;

    public int getId() { return id; }

    public String getName() { return name; }
	public void setName(String name) {
		this.name = name;
	}
}
