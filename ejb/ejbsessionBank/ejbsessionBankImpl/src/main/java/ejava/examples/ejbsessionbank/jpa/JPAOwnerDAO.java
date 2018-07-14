package ejava.examples.ejbsessionbank.jpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.ejbsessionbank.bo.Account;
import ejava.examples.ejbsessionbank.bo.Owner;
import ejava.examples.ejbsessionbank.dao.DAOException;
import ejava.examples.ejbsessionbank.dao.OwnerDAO;

public class JPAOwnerDAO implements OwnerDAO {
    static final Logger log = LoggerFactory.getLogger(JPAOwnerDAO.class);
    
    static final String COUNT_ACCOUNT_REFERENCES_QUERY = 
        "countAccountReferences";
    
    private EntityManager em;

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public Owner createOwner(Owner owner) throws DAOException {
        em.persist(owner);
        return owner;
    }

    @SuppressWarnings("unchecked")
    public List<Owner> findOwners(String queryName, Map<String, Object> params,
            int index, int count) throws DAOException {
        Query query = em.createNamedQuery(queryName);
        if (params != null && params.size() > 0) {
            for (String key : params.keySet()) {
                Object value = params.get(key);
                query.setParameter(key, value);
            }
        }
        return query.setFirstResult(index)
                     .setMaxResults(count)
                     .getResultList();
    }

    public Owner getOwnerById(long id) throws DAOException {
        return em.find(Owner.class, id);
    }

    public Owner removeOwner(Owner owner) throws DAOException {
        if (!em.contains(owner)) {
            owner = em.find(Owner.class, owner.getId());
        }
        if (owner != null) {
            Collection<Account> accounts = new ArrayList<Account>();
            accounts.addAll(owner.getAccounts());
            for (Account a: accounts) {
                owner.getAccounts().remove(a);
    
                //don't delete account if referenced by other owners
                if (((Long)em.createNamedQuery(COUNT_ACCOUNT_REFERENCES_QUERY)
                      .setParameter("account", a)
                      .getSingleResult()) == 0) {
                    em.remove(a);
                }
            }
            em.remove(owner);
        }
        return owner;
    }

	public List<Owner> getAccountOwners(Account account) throws DAOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("account", account);
        List<Owner> owners = 
        	findOwners("getAccountOwner", params, 0, 100);
        return owners;
	}
}
