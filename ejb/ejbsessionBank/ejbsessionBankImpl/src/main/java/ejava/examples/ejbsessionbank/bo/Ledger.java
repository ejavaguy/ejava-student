package ejava.examples.ejbsessionbank.bo;

import java.io.Serializable;

public class Ledger implements Serializable {
    private static final long serialVersionUID = 4664361360609429071L;
    private long numberOfAccounts;
    private double totalAssets;
    private double aveAssets;
    
    public Ledger(
            long numberOfAccounts, double totalAssets, double aveAssets) {
        setNumberOfAccounts(numberOfAccounts);
        setTotalAssets(totalAssets);
        setAverageAssets(aveAssets);
    }
    public double getAverageAssets() {
        return aveAssets;
    }
    private void setAverageAssets(double aveAssets) {
        this.aveAssets = aveAssets;
    }
    public long getNumberOfAccounts() {
        return numberOfAccounts;
    }
    private void setNumberOfAccounts(long numberOfAccounts) {
        this.numberOfAccounts = numberOfAccounts;
    }
    public double getTotalAssets() {
        return totalAssets;
    }
    private void setTotalAssets(double totalAssets) {
        this.totalAssets = totalAssets;
    }
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("# accts=" + numberOfAccounts);
        text.append(", ave $=" + aveAssets);
        text.append(", tot $=" + totalAssets);
        return text.toString();
    }
}
