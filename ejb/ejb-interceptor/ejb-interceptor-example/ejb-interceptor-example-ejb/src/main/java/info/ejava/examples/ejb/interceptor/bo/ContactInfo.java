package info.ejava.examples.ejb.interceptor.bo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@SuppressWarnings("serial")
@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class ContactInfo implements Serializable {
    @Id @GeneratedValue
    @Column(name="INFO_ID")
    private int id;
    
    @NotNull(groups=PrePersistCheck.class)
    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="CONTACT_ID", nullable=false, updatable=false)
    private Contact contact;
    
    @NotNull(groups={PostNormalizedCheck.class, PrePersistCheck.class})
    @Enumerated(EnumType.STRING)
    @Column(name="CONTACT_ROLE", nullable=false)
    private ContactRole role;
    
    public abstract ContactType getContactType();
    
    protected ContactInfo() {}
    public ContactInfo(Contact contact) {
        this.contact = contact;
    }
    public ContactInfo(int id, Contact contact) {
        this.id = id;
        this.contact = contact;
    }
    public int getId() { return id; }
    
    public Contact getContact() { return contact; }
    public void setContact(Contact contact) {
        this.contact = contact;
    }
    
    public ContactRole getRole() { return role; }
    public void setRole(ContactRole role) {
        this.role = role;
    }
}
