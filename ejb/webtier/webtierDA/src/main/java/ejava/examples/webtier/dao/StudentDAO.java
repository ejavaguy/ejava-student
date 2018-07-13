package ejava.examples.webtier.dao;

import java.util.List;
import java.util.Map;

import ejava.examples.webtier.bo.Student;

public interface StudentDAO {
    public Student get(long id);
    public Student create(Student student);
    public Student update(Student student);
    public Student remove(Student student);
    public List<Student> find(int index, int count);
    public List<Student> find(String queryName, int index, int count);
    public List<Student> find(String queryName, Map<String,Object> params, int index, int count);
}
