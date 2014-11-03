package info.ejava.examples.ejb.cdisales.ejb;

import info.ejava.examples.ejb.cdisales.bl.InvalidAccount;
import info.ejava.examples.ejb.cdisales.bo.CurrentUser;
import info.ejava.examples.ejb.cdisales.bo.Member;
import info.ejava.examples.ejb.cdisales.dao.UserMgmtDAO;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class UserMgmtEJB implements UserMgmtLocal {
    private static final Logger logger = LoggerFactory.getLogger(UserMgmtEJB.class);
    
    //UserMgmtDAO is not @Named, we are using a @Produces approach
    @Inject
    private UserMgmtDAO dao;
    
    @PostConstruct
    public void init() {
        logger.debug("*** UserMgmtEJB({}):init ***", super.hashCode());
    }
    
    @PreDestroy
    public void destroy() {
        logger.debug("*** UserMgmtEJB({}):destroy ***", super.hashCode());
    }
    
    /**
     * We can't do much more than anonymous until we implement login
     */
    @Override
    @Produces
    @SessionScoped
    public CurrentUser login() {
        return login(null);
    }

    protected CurrentUser login(String login) {
        Member member = findMemberByLogin(login);
        if (member==null) {
            member = findMemberByLogin("anonymous");
            if (member==null) {
                member = new Member();
                member.setLogin("anonymous");
                member.setName("anonymous");
                member.setEmail("nonone@nowhere");
                try { createMember(member); }
                catch (InvalidAccount ex) { 
                    throw new EJBException("unexpected error creating bootstrap anonymous account", ex); 
                }
            }
        }
        CurrentUser currentUser = new CurrentUser();
        currentUser.setMember(member);
        return currentUser;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Member createMember(Member member) throws InvalidAccount {
        return dao.createMember(member);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Member getMember(int id) {
        return dao.getMember(id);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Member findMemberByLogin(String login) {
        return dao.findMemberByLogin(login);
    }
}
