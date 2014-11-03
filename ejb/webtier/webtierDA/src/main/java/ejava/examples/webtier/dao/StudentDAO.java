package ejava.examples.webtier.dao;

import java.util.List;
import java.util.Map;

import ejava.examples.webtier.bo.Student;

public interface StudentDAO {
    public Student get(long id) throws StudentDAOException;
    public Student create(Student student) throws StudentDAOException;
    public Student update(Student student)  throws StudentDAOException;
    public Student remove(Student student) throws StudentDAOException;
    public List<Student> find(int index, int count) throws StudentDAOException;
    public List<Student> find(String queryName, int index, int count) 
        throws StudentDAOException;
    public List<Student> find(
            String queryName, Map<String,Object> params, int index, int count) 
        throws StudentDAOException;
}
