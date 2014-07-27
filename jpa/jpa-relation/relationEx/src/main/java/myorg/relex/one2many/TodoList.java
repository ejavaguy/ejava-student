package myorg.relex.one2many;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
/**
 * This class provides an example owning entity in a one-to-many, uni-directional relationship 
 * where the members of the collection are subject to orphanRemoval when they are removed from the 
 * collection. 
 */
@Entity
@Table(name="RELATIONEX_TODOLIST")
public class TodoList {
	@Id @GeneratedValue
	private int id;
	
	@OneToMany(cascade={CascadeType.PERSIST}
			,orphanRemoval=true
		)
	@JoinColumn
	private List<Todo> todos;

	public int getId() { return id; }

	public List<Todo> getTodos() {
		if (todos==null) {
			todos = new ArrayList<Todo>();
		}
		return todos;
	}
	public void setTodos(List<Todo> todos) {
		this.todos = todos;
	}
}
