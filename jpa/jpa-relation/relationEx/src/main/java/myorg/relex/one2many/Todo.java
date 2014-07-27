package myorg.relex.one2many;

import javax.persistence.*;
/**
 * This class is an example of the many side of a one-to-many, uni-directional relationship 
 * which uses orphanRemoval of target entities on the many side. This entity exists for the 
 * sole use of the one side of the relation.
 */
@Entity
@Table(name="RELATIONEX_TODO")
public class Todo {
	@Id @GeneratedValue
	private int id;
	
	@Column(length=32)
	private String title;
	
	public Todo() {}
	public Todo(String title) {
		this.title = title;
	}

	public int getId() { return id; }

	public String getTitle() { return title; }
	public void setTitle(String title) {
		this.title = title;
	}
}
