package ejava.examples.orm.core.mapped;

/**
 * This class provides an example of embedding a primary key class within
 * the containing class.
 */
public class Napsack {
    private NapsackPK pk;
    private int size;
    
    public Napsack() {}
    public Napsack(String make, String model) {
        this.pk = new NapsackPK(make, model);
    }
    
    public NapsackPK getPk() { return pk; }
    
    public int getSize() { return size; }
    public void setSize(int size) {
        this.size = size;
    }
    
    public String toString() {
        return super.toString() +
           "pk=" + pk +
           ", size=" + size;
    }
}
