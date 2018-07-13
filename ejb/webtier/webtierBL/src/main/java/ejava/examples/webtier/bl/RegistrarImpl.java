package ejava.examples.webtier.bl;

import java.util.List;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.webtier.bo.Grade;
import ejava.examples.webtier.bo.Student;
import ejava.examples.webtier.dao.StudentDAO;

public class RegistrarImpl implements Registrar {
    Logger logger = LoggerFactory.getLogger(RegistrarImpl.class);
    private static final String NEW_STUDENT_QUERY = "getNewStudents";
    private static final String GRAD_QUERY = "getGraduatingStudents";
    
    private StudentDAO dao;
    
    public void setStudentDAO(StudentDAO dao) {
        this.dao = dao;
    }

    public Student addStudent(Student student) throws RegistrarException {
        try {
            return dao.create(student);
        }
        catch (Throwable th) {
            throw new RegistrarException(th);
        }
    }

    public Student completeCourse(Student student, Grade grade)
            throws RegistrarException {
        try {
            student.getGrades().add(grade);            
            return dao.update(student);
        }
        catch (Throwable th) {
            throw new RegistrarException(th);
        }
    }

    public Student dropStudent(Student student) throws RegistrarException {
        try {
            //do some checking around.....
            //if okay
            return dao.remove(student);
        }
        catch (Throwable th) {
            throw new RegistrarException(th);
        }
    }

    public List<Student> getStudents(int index, int count)
        throws RegistrarException {
        try {
            return dao.find(index, count);
        }
        catch (Throwable th) {
            throw new RegistrarException(th);
        }
    }
    
    public List<Student> getGraduatingStudents(int index, int count)
            throws RegistrarException {
        try {
            return dao.find(GRAD_QUERY, null, index, count);
        }
        catch (Throwable th) {
            throw new RegistrarException(th);
        }
    }

    public List<Student> getNewStudents(int index, int count)
            throws RegistrarException {
        try {
            return dao.find(NEW_STUDENT_QUERY, null, index, count);
        }
        catch (Throwable th) {
            throw new RegistrarException(th);
        }
    }

    public Student getStudent(long id) throws RegistrarException {
        try {
            return dao.get(id); 
        }
        catch (Throwable th) {
            throw new RegistrarException(th);
        }
    }

    public List<Student> getStudents(
            String queryName, Map<String, Object> params, int index, int count) 
            throws RegistrarException {
        try {
            return dao.find(queryName, params, index, count);
        }
        catch (Throwable th) {
            throw new RegistrarException(th);
        }
    }
}
