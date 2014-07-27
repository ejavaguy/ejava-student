package ejava.examples.orm.inheritance.annotated;

import javax.persistence.*;

/**
 * This class provides an example of a non-entity base class that can be 
 * mapped into tables defined by entities that derive from this class.
 */
@MappedSuperclass
public abstract class BaseObject {
    private long id;
    @Access(AccessType.FIELD)
    private long version;
    
    @Transient
    public long getId() { return id; }
    protected void setId(long id) {
        this.id = id;
    }
    
    public long getVersion() { return version; }
    public void setVersion(long version) {
        this.version = version;
    }
    
    @Transient
    public abstract String getName();
    
    public String toString() {
        StringBuilder text = new StringBuilder(super.toString());
        text.append(", id=" + id);
        text.append(", name=" + getName());
        return text.toString();
    }
}
