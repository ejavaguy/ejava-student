package ejava.examples.ejbsessionbank.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class Owner implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
    private String firstName;
    private String lastName;
    private String ssn;
    private Collection<Account> accounts = new ArrayList<Account>();

    public Owner() {}
    public Owner(long id) {
        this.id = id;
    }
    public Owner(long id, String firstName, String lastName) {
        this(id);
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    public long getId() {
        return id;
    }
    protected void setId(long id) {
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
    public String getSsn() {
        return ssn;
    }
    public void setSsn(String ssn) {
        this.ssn = ssn;
    }
    public Collection<Account> getAccounts() {
        return accounts;
    }
    public void setAccounts(Collection<Account> accounts) {
        this.accounts = accounts;
    }
   
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("id=" + id);
        text.append(", " + firstName + " " + lastName);
        text.append(", ssn=" + ssn);
        text.append(", accounts("+ accounts.size() + ")={");
        for(Account a : accounts) {
            text.append(a + ", ");
        }
        text.append("}");
        return text.toString();
    }
}
