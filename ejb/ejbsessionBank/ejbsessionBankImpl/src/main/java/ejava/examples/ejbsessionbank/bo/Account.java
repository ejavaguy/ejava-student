package ejava.examples.ejbsessionbank.bo;

import java.io.Serializable;

public class Account implements Serializable {
    private static final long serialVersionUID = -610133933394333468L;
    private long id;
    private String accountNumber;
    private double balance;
    
    public Account() {}
    public Account(long id) { setId(id); }
    public Account(long id, String accountNumber) {
        setId(id);
        setAccountNumber(accountNumber);
    }
    
    public long getId() {
        return id;
    }
    private void setId(long id) {
        this.id = id;
    }
    public void deposit(double amount) {
        setBalance(getBalance() + amount); 
    }
    public void withdraw(double amount) {
        setBalance(getBalance() - amount); 
    }    
    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    public double getBalance() {
        return balance;
    }
    private void setBalance(double balance) {
        this.balance = balance;
    }        
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("id=" + id);
        text.append(", acctnum=" + accountNumber);
        text.append(", bal=$" + balance);
        return text.toString();
    }
}
