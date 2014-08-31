package ejava.examples.ejbsessionbank.dto;

import java.io.Serializable;

public class OwnerDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private String firstName;
    private String lastName;
    private int accounts;

    public OwnerDTO() {}
    public OwnerDTO(long id) {
        this.id = id;
    }
    public OwnerDTO(long id, String firstName, String lastName, int accounts) {
        this(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.accounts = accounts;
    }
    
    public long getId() {
        return id;
    }
    public void setId(long id) {
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
    public int getAccounts() {
        return accounts;
    }
    public void setAccounts(int accounts) {
        this.accounts = accounts;
    }
    
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("id=" + id);
        text.append(", firstName=" + firstName);
        text.append(", lastName=" + lastName);
        text.append(", accounts=" + accounts);
        return text.toString();
    }
}
