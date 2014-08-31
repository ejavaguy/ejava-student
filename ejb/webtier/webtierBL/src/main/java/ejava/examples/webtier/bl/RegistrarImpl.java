package ejava.examples.webtier.bl;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.webtier.bo.Grade;
import ejava.examples.webtier.bo.Student;
import ejava.examples.webtier.dao.DAOFactory;
import ejava.examples.webtier.dao.DAOTypeFactory;
import ejava.examples.webtier.dao.StudentDAO;

public class RegistrarImpl implements Registrar {
    Log log = LogFactory.getLog(RegistrarImpl.class);
    private static final String NEW_STUDENT_QUERY = "getNewStudents";
    private static final String GRAD_QUERY = "getGraduatingStudents";
    
    private StudentDAO dao = null;
    private StudentDAO getDAO() throws RegistrarException {
        if (dao == null) {
            DAOTypeFactory daoType = DAOFactory.getDAOTypeFactory();
            log.debug("RegistrarImpl got daoTypeFactory:" + daoType);
            if (daoType != null) {
               dao = daoType.getStudentDAO();
            }
            else {
                throw new RegistrarException("unable to get DAO");
            }
        }
        return dao;
    }

    public Student addStudent(Student student) throws RegistrarException {
        try {
            return getDAO().create(student);
        }
        catch (Throwable th) {
            throw new RegistrarException(th);
        }
    }

    public Student completeCourse(Student student, Grade grade)
            throws RegistrarException {
        try {
            student.getGrades().add(grade);            
            return getDAO().update(student);
        }
        catch (Throwable th) {
            throw new RegistrarException(th);
        }
    }

    public Student dropStudent(Student student) throws RegistrarException {
        try {
            //do some checking around.....
            //if okay
            return getDAO().remove(student);
        }
        catch (Throwable th) {
            throw new RegistrarException(th);
        }
    }

    public List<Student> getStudents(int index, int count)
        throws RegistrarException {
        try {
            return getDAO().find(index, count);
        }
        catch (Throwable th) {
            throw new RegistrarException(th);
        }
    }
    
    public List<Student> getGraduatingStudents(int index, int count)
            throws RegistrarException {
        try {
            return getDAO().find(GRAD_QUERY, null, index, count);
        }
        catch (Throwable th) {
            throw new RegistrarException(th);
        }
    }

    public List<Student> getNewStudents(int index, int count)
            throws RegistrarException {
        try {
            return getDAO().find(NEW_STUDENT_QUERY, null, index, count);
        }
        catch (Throwable th) {
            throw new RegistrarException(th);
        }
    }

    public Student getStudent(long id) throws RegistrarException {
        try {
            return getDAO().get(id); 
        }
        catch (Throwable th) {
            throw new RegistrarException(th);
        }
    }

    public List<Student> getStudents(
            String queryName, Map<String, Object> params, int index, int count) 
            throws RegistrarException {
        try {
            return getDAO().find(queryName, params, index, count);
        }
        catch (Throwable th) {
            throw new RegistrarException(th);
        }
    }

}
