package info.ejava.examples.ejb.cdisales.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import info.ejava.examples.ejb.cdisales.bl.InvalidAccount;
import info.ejava.examples.ejb.cdisales.bl.UserMgmt;
import info.ejava.examples.ejb.cdisales.bo.Member;

import javax.enterprise.inject.Alternative;
import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

/**
 * This bean will be "Produced" and injected from a factory
 */
@Alternative
public class UserMgmtDAO implements UserMgmt {
    private EntityManager em;
    private Validator validator;
    
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
    public void setValidator(Validator validator) {
        this.validator=validator;
    }

    @Override
    public Member createMember(Member member) throws InvalidAccount {
        Set<ConstraintViolation<Member>> errors = validator.validate(member);
        if (errors!=null && !errors.isEmpty()) {
            List<String> errorText = new ArrayList<String>(errors.size());
            for (ConstraintViolation<Member> v: errors) {
                errorText.add(v.toString());
            }
            throw new InvalidAccount("invalid account:" + errorText);
        }
        
        if (findMemberByLogin(member.getLogin())!=null) {
            throw new InvalidAccount(String.format("login [%s] already exists", member.getLogin()));
        }
        
        em.persist(member);
        return member;
    }

    @Override
    public Member getMember(int id) {
        return em.find(Member.class, id);
    }

    @Override
    public Member findMemberByLogin(String login) {
        if (login==null) { return null; }
        List<Member> members = em.createNamedQuery("CDIMember.getMemberByLogin",Member.class)
                .setParameter("login", login)
                .getResultList();
        return members.isEmpty() ? null : members.get(0);  
    }
}
