package ejava.examples.ejbsessionbank.dao;

import java.util.List;
import java.util.Map;

import ejava.examples.ejbsessionbank.bo.Account;
import ejava.examples.ejbsessionbank.bo.Owner;

public interface OwnerDAO {
    String GET_OWNERS_QUERY = "getAccountOwners";    

    Owner getOwnerById(long id) throws DAOException;
    List<Owner> getAccountOwners(Account account) throws DAOException;
    Owner createOwner(Owner owner) throws DAOException;
    Owner removeOwner(Owner owner) throws DAOException;
    List<Owner> findOwners(String queryName, Map<String, Object> params,
            int index, int count) throws DAOException;
}