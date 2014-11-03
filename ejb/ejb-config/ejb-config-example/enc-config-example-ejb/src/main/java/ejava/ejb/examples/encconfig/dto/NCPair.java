package ejava.ejb.examples.encconfig.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class NCPair implements Serializable {
	private String name;
	private String className;

	public NCPair() {}
	public NCPair(String name, String className) {
		this.name = name;
		this.className = className;
	}
	
	public String getName() { return name; }
	public void setName(String name) {
		this.name = name;
	}
	
	public String getClassName() { return className; }
	public void setClassName(String className) {
		this.className = className;
	}
	
	@Override
	public String toString() {
		return name + ":" + className;
	}
}
