package myorg.relex.many2many;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.*;

/**
 * This class provides an example of a many-to-many, bi-directional relationship that just
 * happens to be recursive. Both ends of the relationship reference an instance of this class. 
 */
@Entity
@Table(name="RELATIONEX_NODE")
public class Node {
	@Id
	@Column(length=36, nullable=false)
	private String id;
	
	@ManyToMany(cascade={CascadeType.PERSIST}, fetch=FetchType.LAZY)
	@JoinTable(name="RELATIONEX_NODE_REL",
			joinColumns=@JoinColumn(name="PARENT_ID"),
			inverseJoinColumns=@JoinColumn(name="CHILD_ID"))
	private Set<Node> children;
	
	@ManyToMany(mappedBy="children", fetch=FetchType.EAGER)
	private Set<Node> parents;
	
	@Column(length=32, nullable=false)
	private String name;
	
	public Node() { id=UUID.randomUUID().toString(); }
	public Node(String name) {
		this();
		this.name = name;
	}
	public Node(Node parent, String name) {
		this();
		this.name = name;
		parent.getChildren().add(this);
		getParents().add(parent);
	}

	public String getId() { return id; }
	public String getName() { return name; }
	public void setName(String name) {
		this.name = name;
	}

	public Set<Node> getChildren() {
		if (children==null) {
			children = new HashSet<Node>();
		}
		return children;
	}
	
	public Set<Node> getParents() {
		if (parents == null) {
			parents = new HashSet<Node>();
		}
		return parents;
	}
	
	@Override
	public int hashCode() {
		return id==null?0:id.hashCode();
	}	
	@Override
	public boolean equals(Object obj) {
		try {
			if (this == obj) { return true; }
			Node rhs = (Node)obj;
			if (id==null && rhs.id != null) { return false; }
			return id.equals(rhs.id);
		} catch (Exception ex) { return false; }
	}
}
