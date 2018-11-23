package ejava.examples.asyncmarket.ejb;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.asyncmarket.bo.Person;
import ejava.examples.asyncmarket.dao.PersonDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class UserMgmtEJB implements UserMgmtRemote, UserMgmtLocal {
    private static final Logger logger = LoggerFactory.getLogger(UserMgmtEJB.class);

    @Inject
    private PersonDAO userDAO;
    @Inject
    private DtoMapper dtoMapper;
    
    @PostConstruct
    public void init() {
        logger.info("*** UserMgmtEJB init() ***");
    }

    public long createUser(String userId, String name) {
        try {
            Person user = new Person();
            user.setName(name);
            user.setUserId(userId);
            return userDAO.createPerson(user).getId();
        } 
        catch (Exception ex) {
            logger.error("error creating user", ex);
            throw new InternalErrorException("error creating user:%s", ex);
        }
    }

    public List<Person> getUsers(int index, int count) {
        try {
            return dtoMapper.toDTOPeople(userDAO.getPeople(index, count));
        }
        catch (Exception ex) {
            logger.error("error getting users", ex);
            throw new InternalErrorException("error getting users: %s", ex);
        }
    }

    public Person getUser(long id) throws ResourceNotFoundException {
        Person user = userDAO.getPerson(id);
        if (user==null) {
            throw new ResourceNotFoundException("userId[%d] not found", id);
        }
        
        try {
            return dtoMapper.toDTO(user);
        }
        catch (Exception ex) {
            logger.error("error getting user", ex);
            throw new InternalErrorException("error getting user: %s", ex);
        }
    }

    public Person getUserByUserId(String userId) throws ResourceNotFoundException {
        Person user = userDAO.getPersonByUserId(userId);
        if (user==null) {
            throw new ResourceNotFoundException("userId[%s] not found", userId);
        }
        logger.debug("getUserByUserId({})={}", userId, user);
        
        try {
            return dtoMapper.toDTO(user);
        }
        catch (Exception ex) {
            logger.error("error getting user by userId", ex);
            throw new InternalErrorException("error getting user by userId: %s", ex);
        }
    }

    public void removeUser(String userId) throws ResourceNotFoundException {
        Person user = userDAO.getPersonByUserId(userId);
        if (user==null) {
            throw new ResourceNotFoundException("userId[%s] not found", userId);            
        }
        
        try {
            userDAO.removePerson(user);
        }
        catch (Exception ex) {
            logger.error("error getting user by userId", ex);
            throw new InternalErrorException("error getting user by userId: %s", ex);
        }
    }
}
