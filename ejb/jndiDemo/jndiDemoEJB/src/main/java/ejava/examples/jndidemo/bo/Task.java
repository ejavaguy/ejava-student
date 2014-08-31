package ejava.examples.jndidemo.bo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="JNDI_TASK")
public class Task {
	@Id
	private int id;
	private String name;
	
	
	public Task() {}
	public Task(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("id=").append(id)
		       .append(", name=").append(name);
		return builder.toString();
	}
}
