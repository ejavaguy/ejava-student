package myorg.relex.collection;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.*;

/**
 * This class provides an example of a parent that uses a Map to reference child members.
 */
@Entity
@Table(name="RELATIONEX_LINEUP")
public class Lineup {
	@Id @GeneratedValue
	private int id;
	
	@OneToMany
	@MapKey(name="position")
	@JoinColumn(name="LINEUP_ID")
	private Map<String, Position> positions;
	
	@Column(length=10)
	private String team;

	public int getId() { return id; }

	public Map<String, Position> getPositions() {
		if (positions==null) { positions = new HashMap<String, Position>(); }
		return positions; 
	}
	public Lineup addPosition(Position position) {
		if (position==null) { return this; }
		getPositions().put(position.getPosition(), position);
		return this;
	}

	public String getTeam() { return team; }
	public void setTeam(String team) {
		this.team = team;
	}
}
