package myorg.relex.one2many;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

/**
 * This class provides an example of the one side of a one-to-many, uni-directional relationship 
 * mapped using a foreign key inserted into the child/many table. The @JoinColumn is referencing 
 * the child table and not this entity's table.
 */
@Entity
@Table(name="RELATIONEX_ROUTE")
public class Route {
    @Id 
    private int number;
    
    @OneToMany
    @JoinColumn
    private List<Stop> stops;

    protected Route() {}
	public Route(int number) {
		this.number = number;
	}

	public int getNumber() { return number; }

	public List<Stop> getStops() {
		if (stops == null) {  stops = new ArrayList<Stop>(); }
		return stops;
	}
	public void setStops(List<Stop> stops) {
		this.stops = stops;
	}
}
