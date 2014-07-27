package ejava.examples.orm.inheritance.annotated;

import javax.persistence.*;

/**
 * This class provides an example of an entity sub-class using separate
 * classes per-concrete derived class. This means that the base class
 * properties will get merged into this table (similar to non-entity 
 * inheritance).
 */
@Entity
@Table(name="ORMINH_CHECKING")
public class CheckingAccount extends Account {
    private double fee;
    
    public CheckingAccount() {}
    public CheckingAccount(long id) { super(id); }

    public void withdraw(double amount) throws AccountException {
        super.setBalance(super.getBalance() - fee);
    }

    public double getFee() { return fee; }
    public void setFee(double fee) {
        this.fee = fee;
    }
    
    public String toString() {
        StringBuilder text = new StringBuilder(super.toString());
        text.append(", fee=" + fee);
        return text.toString();
    }
}
