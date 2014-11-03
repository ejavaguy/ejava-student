package ejava.examples.txhotel.bo;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class Person implements Serializable {
    private long id;
    private long version;
    private String firstName;
    private String lastName;
    private Date creationDate=new Date();
    
    public Person() {}
    public Person(long id, long version, String firstName, String lastName) { 
        setId(id);
        setVersion(version);
        setFirstName(firstName);
        setLastName(lastName);
    }
    
    public long getId() {
        return id;
    }
    private void setId(long id) {
        this.id = id;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }    
    public long getVersion() {
        return version;
    }
    public void setVersion(long version) {
        this.version = version;
    }
    
    @Override
	public boolean equals(Object obj) {
    	try {
    		if (this==obj) return true;
    		Person rhs = (Person)obj;
    		return creationDate.getTime()==rhs.creationDate.getTime() &&
    				firstName.equals(rhs.firstName) &&
    				lastName.equals(rhs.lastName);
    	} catch (Exception ex) {
    		return false;
    	}
	}
	@Override
	public int hashCode() {
		return creationDate.hashCode();
	}
	
	public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("id=" + id);
        text.append(", version=" + version);
        text.append(", firstName=" + firstName);
        text.append(", lastName=" + lastName);
        return text.toString();
    }
}