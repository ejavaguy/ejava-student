package ejava.examples.webtier.dao;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import ejava.examples.webtier.dao.DAOFactory;
import ejava.examples.webtier.dao.DAOTypeFactory;
import ejava.examples.webtier.dao.StudentDAO;
import ejava.examples.webtier.dao.StudentDAOException;
import ejava.examples.webtier.bo.Student;
import junit.framework.TestCase;

public class DAOFactoryTest  {
    Log log = LogFactory.getLog(DAOFactoryTest.class);

    @Test
    public void testDAOFactory() throws Exception {
        log.info("*** testDAOFactory ***");
        TestDAOFactory.class.newInstance();
        DAOTypeFactory daoFactory = DAOFactory.getDAOTypeFactory();
        assertNotNull("type factory was null", daoFactory);
        
        StudentDAO dao = daoFactory.getStudentDAO();
        assertNotNull("factory was null", dao);
        
        assertNull("unexpected result", dao.get(0L));
        
    }
    
    
    public static class TestDAOFactory implements DAOTypeFactory {
        public static final String NAME = "Test";
        static {
            DAOFactory.registerFactoryType(NAME, new TestDAOFactory());
        }
        public String getName() {
            return NAME;
        }
        public StudentDAO getStudentDAO() {
            return new TestDAO();
        }        
    }
    
    public static class TestDAO implements StudentDAO {
        public Student create(Student student) throws StudentDAOException {
            return null;
        }

        public List<Student> find(int index, int count) 
            throws StudentDAOException {
            return null;
        }
        public List<Student> find(
                String name, Map<String, Object> params, int index, int count) 
            throws StudentDAOException {
            return null;
        }

        public Student get(long id) throws StudentDAOException {
            return null;
        }

        public Student remove(Student student) throws StudentDAOException {
            return null;
        }

        public Student update(Student student) throws StudentDAOException {
            return null;
        }

        public List<Student> find(String queryName, int index, int count) 
            throws StudentDAOException {
            return null;
        }        
    }

}
