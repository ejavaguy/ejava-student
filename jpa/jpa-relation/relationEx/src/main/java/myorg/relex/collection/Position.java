package myorg.relex.collection;

import javax.persistence.*;

/**
 * This class is an example of an entity that will be referenced from the parent in its relationship
 * through a Map which uses a value unique to that parent.
 */
@Entity
@Table(name="RELATIONEX_POSITION")
public class Position {
	@Id @GeneratedValue
	private int id;
	
	@Column(length=12, nullable=false)
	private String position; //this is not unique within this table
	
	@Column(length=32, nullable=false, unique=true)
	private String player; //this is unique within the table
	
	protected Position() {}
	public Position(String position, String player) {
		this.position = position;
		this.player = player;
	}

	public int getId() { return id; }

	public String getPosition() { return position; }
	public void setPosition(String position) { this.position = position; }

	public String getPlayer() { return player; }
	public void setPlayer(String player) {
		this.player = player;
	}
	
	@Override
	public int hashCode() {
		return position==null?0:position.hashCode() + player==null?0:player.hashCode(); 
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			if (this == obj) { return true; }
			Position rhs = (Position) obj;
			if (position==null || player==null) { return false; }
			return position.equals(rhs.position) && player.equals(rhs.player);
		} catch (Exception ex) { return false; }
	}
}
