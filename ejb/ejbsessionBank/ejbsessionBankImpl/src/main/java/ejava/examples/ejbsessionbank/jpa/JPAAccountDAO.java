package ejava.examples.ejbsessionbank.jpa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.ejbsessionbank.bo.Account;
import ejava.examples.ejbsessionbank.bo.Ledger;
import ejava.examples.ejbsessionbank.dao.AccountDAO;
import ejava.examples.ejbsessionbank.dao.DAOException;

public class JPAAccountDAO implements AccountDAO {
    private static Logger log = LoggerFactory.getLogger(JPAAccountDAO.class); 
    public static final String ACCOUNT_BY_NUM = "getAccountsByAccountNumber"; 
    public static final String ACCOUNT_NUM_PARAM = "accountNumber"; 
    public static final String GET_LEDGER = "getLedger"; 
    public static final String GET_LEDGER_AVE = "getLedgerAveBalance"; 
    public static final String GET_LEDGER_CNT = "getLedgerCount"; 
    public static final String GET_LEDGER_SUM = "getLedgerSum"; 
    
    private EntityManager em;
    
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public Account createAccount(Account account) throws DAOException {
        try {
            em.persist(account);
            return account;
        }
        catch (Throwable ex) {
            log.error("error in createAccount", ex);
            throw new DAOException(
                    "error creating account:" + account,ex);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Account> findAccounts(String queryName,
            Map<String, Object> params, int index, int count)
            throws DAOException {
        try {
            Query query = 
                em.createNamedQuery(queryName)
                   .setFirstResult(index)
                   .setMaxResults(count);
            if (params != null && params.size() != 0) {
                for(String key: params.keySet()) {
                    query.setParameter(key, params.get(key));
                }
            }
            log.debug("named query:" + queryName + ", params=" + params);
            return query.getResultList();
        }
        catch (Throwable ex) {
            log.error("error in findAccounts", ex);
            throw new DAOException(
                    "error executing named query:" + queryName,ex);
        }
    }

    public Account getAccountById(long id) throws DAOException {
        try {
            return em.find(Account.class, id);
        }
        catch (Throwable ex) {
            log.error("error in getAccountById", ex);
            throw new DAOException(
                    "error finding account:" + id,ex);
        }
    }

    public Account getAccountByNum(String acctNum) throws DAOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(ACCOUNT_NUM_PARAM, acctNum);
        List<Account> accounts = findAccounts(ACCOUNT_BY_NUM, params, 0, 1);
        return (accounts.size() == 1) ? accounts.get(0) : null;
    }

    public Account removeAccount(Account account) throws DAOException {
        try {
            if (!em.contains(account)) {
                account = em.find(Account.class, account.getId());
            }
            if (account != null) {
                em.remove(account);
            }
            return account;
        }
        catch (Throwable ex) {
            log.error("error in removeAccount", ex);
            throw new DAOException(
                    "error removing account:" + account,ex);
        }
    }

    public Account updateAccount(Account account) throws DAOException {
        try {
            return em.merge(account);
        }
        catch (Throwable ex) {
            log.error("error in updateAccount", ex);
            throw new DAOException(
                    "error removing account:" + account,ex);
        }
    }

    public Ledger getLedger() throws DAOException {
        try {
            return (Ledger) em.createNamedQuery(GET_LEDGER)
                                  .getSingleResult();
        }
        catch (Throwable ex) {
            log.error("error in getLedger", ex);
            throw new DAOException(
                    "error getting ledger",ex);
        }
    }

    public double getLedgerAveBalance() throws DAOException {
        try {
            return (Double) em.createNamedQuery(GET_LEDGER_AVE)
                               .getSingleResult();
        }
        catch (Throwable ex) {
            log.error("error in getLedgerBalance", ex);
            throw new DAOException(
                    "error getting ledger ave balance",ex);
        }
    }

    public long getLedgerCount() throws DAOException {
        try {
            Object count =  em.createNamedQuery(GET_LEDGER_CNT)
                              .getSingleResult();
            log.error("getLedgerCount data type=" + count.getClass());            
            return ((Long)count).longValue();
        }
        catch (Throwable ex) {
            log.error("error in getLedgerCount", ex);
            throw new DAOException(
                    "error getting ledger count",ex);
        }
    }

    public double getLedgerSum() throws DAOException {
        try {
            return (Double) em.createNamedQuery(GET_LEDGER_SUM)
                               .getSingleResult();
        }
        catch (Throwable ex) {
            log.error("error in getLedgerSum", ex);
            throw new DAOException(
                    "error getting ledger sum",ex);
        }
    }
}
