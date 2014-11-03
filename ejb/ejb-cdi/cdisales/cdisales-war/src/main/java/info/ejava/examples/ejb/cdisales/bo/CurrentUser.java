package info.ejava.examples.ejb.cdisales.bo;

import java.io.Serializable;

import javax.enterprise.inject.Alternative;

@Alternative
public class CurrentUser implements Serializable {
    private Member member;

    public void setMember(Member member) {
        this.member = member;
    }
    
    public String getName() {
        return member==null ? null : member.getName();
    }
    
    public String getLogin() {
        return member==null ? null : member.getLogin();
    }

    public Member getMember() {
        return member;
    }
}
