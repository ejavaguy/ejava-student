package info.ejava.examples.ejb.cdisales.dao;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Validator;

public class DAOFactory {
    @PersistenceContext(unitName="ejbcdi-sales")
    @Produces
    public EntityManager em;
    
    @Produces
    @Dependent
    public UserMgmtDAO getUserMgmtDAO(EntityManager em, Validator validator) {
        UserMgmtDAO dao = new UserMgmtDAO();
        dao.setEntityManager(em);
        dao.setValidator(validator);
        return dao;
    }
}
