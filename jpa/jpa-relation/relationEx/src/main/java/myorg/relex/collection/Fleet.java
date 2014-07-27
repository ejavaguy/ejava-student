package myorg.relex.collection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.*;

/**
 * This class provides an example one/parent entity with a relationship to many child/dependent
 * objects -- with the members in each collection based on a different hashCode/equals method.
 */
@Entity
@Table(name="RELATIONEX_FLEET")
public class Fleet {
	@Id @GeneratedValue
	private int id;
	@Column(length=16)
	private String name;
	@OneToMany(cascade=CascadeType.PERSIST) 
	@JoinColumn
	private List<ShipByDefault> shipsListByDefault = new ArrayList<ShipByDefault>();
	
	@OneToMany(cascade=CascadeType.PERSIST)  
	@JoinColumn
	private Set<ShipByDefault> shipsSetByDefault = new HashSet<ShipByDefault>();

	
	@OneToMany(cascade=CascadeType.PERSIST) 
	@JoinColumn
	private List<ShipByPK> shipsListByPK = new ArrayList<ShipByPK>();
	
	@OneToMany(cascade=CascadeType.PERSIST)  
	@JoinColumn
	private Set<ShipByPK> shipsSetByPK = new HashSet<ShipByPK>();


	@OneToMany(cascade=CascadeType.PERSIST) 
	@JoinColumn
	private List<ShipBySwitch> shipsListBySwitch = new ArrayList<ShipBySwitch>();
	
	@OneToMany(cascade=CascadeType.PERSIST)  
	@JoinColumn
	private Set<ShipBySwitch> shipsSetBySwitch = new HashSet<ShipBySwitch>();


	@OneToMany(cascade=CascadeType.PERSIST) 
	@JoinColumn
	private List<ShipByBusinessId> shipsListByBusinessId = new ArrayList<ShipByBusinessId>();
	
	@OneToMany(cascade=CascadeType.PERSIST)  
	@JoinColumn
	private Set<ShipByBusinessId> shipsSetByBusinessId = new HashSet<ShipByBusinessId>();

	public int getId() { return id; }
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() { return name;}
	public void setName(String name) {
		this.name = name;
	}
	
	public List<ShipByDefault> getShipsListByDefault() { return shipsListByDefault; }
	public Set<ShipByDefault> getShipsSetByDefault() { return shipsSetByDefault; }

	public List<ShipByPK> getShipsListByPK() { return shipsListByPK; }
	public Set<ShipByPK> getShipsSetByPK() { return shipsSetByPK; }

	public List<ShipBySwitch> getShipsListBySwitch() { return shipsListBySwitch; }
	public Set<ShipBySwitch> getShipsSetBySwitch() { return shipsSetBySwitch; }

	public List<ShipByBusinessId> getShipsListByBusinessId() { return shipsListByBusinessId; }
	public Set<ShipByBusinessId> getShipsSetByBusinessId() { return shipsSetByBusinessId; }
}
