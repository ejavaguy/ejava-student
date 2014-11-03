package ejava.examples.webtier.bl;

import java.util.List;
import java.util.Map;

import ejava.examples.webtier.bo.Grade;
import ejava.examples.webtier.bo.Student;

public interface Registrar {
    List<Student> getStudents(int index, int count) 
        throws RegistrarException;
    List<Student> getNewStudents(int index, int count)  
        throws RegistrarException;
    List<Student> getGraduatingStudents(int index, int count)
        throws RegistrarException;
    List<Student> getStudents(
        String queryName, Map<String, Object> params, int index, int count)
        throws RegistrarException;
    
    Student addStudent(Student student) 
        throws RegistrarException;
    Student getStudent(long id) 
        throws RegistrarException;
    Student completeCourse(Student student, Grade grade) 
        throws RegistrarException;
    Student dropStudent(Student student) 
        throws RegistrarException;
}
