package info.ejava.examples.ejb.cdisales.bo;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="CDISALES_MEMBER")
@NamedQueries({
    @NamedQuery(name="CDIMember.getMemberByLogin", 
        query="select m from Member m where m.login=:login")
})
public class Member implements Comparable<Member>, Serializable {
    private static final long serialVersionUID = -2348898686516963520L;

    @Id @GeneratedValue
    @Column(name="MEMBER_ID")
    private int id;
    
    @NotNull
    @Column(name="LOGIN", length=16, nullable=false, unique=true)
    private String login;
    
    @NotNull
    @Column(name="NAME", length=32, nullable=false)
    private String name;
    
    @Column(name="EMAIL", length=32)
    private String email;

    public Member() {}
    public Member(int id) {
        this.id=id;
    }
    
    public int getId() { return id; }

    public String getLogin() { return login; }
    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = email;
    }
    
    @Override
    public int compareTo(Member rhs) {
        if (rhs==null || name!=null && rhs.name==null) { return -1; }
        if (name==null || rhs.name!=null) { return 1; }
        return name.compareTo(rhs.name);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((login == null) ? 0 : login.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        Member other = (Member) obj;
        
        return (login==null ? other.login==null : login.equals(other.login));
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Member [id=").append(id)
                .append(", login=").append(login)
                .append(", name=").append(name)
                .append(", email=").append(email)
                .append("]");
        return builder.toString();
    }
    
    
}
