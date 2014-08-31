package ejava.examples.webtier.bl;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import ejava.examples.webtier.bl.RegistrarException;
import ejava.examples.webtier.bo.Grade;
import ejava.examples.webtier.bo.Student;
import ejava.examples.webtier.jpa.JPAUtil;

public class BusinessLogicDemo extends DemoBase {

    public void testAddStudent() throws RegistrarException {
        log.info("*** testAddStudent ***");
        
        Student student = new Student();
        student.setFirstName("cat");
        student.setLastName("inhat");
        registrar.addStudent(student);
    }
    
    public void testCompleteCourse() throws RegistrarException {
        log.info("*** testCompleteCourse ***");
        Calendar cal = new GregorianCalendar();
        
        Student student = new Student();
        student.setFirstName("cat");
        student.setLastName("inhat");
        registrar.addStudent(student);
        
        //not good enough...
        for(int i=0; i<10; i++) {
            Grade grade = new Grade();
            grade.setCourse("605.00" + i);
            cal.set(2000 + i, 2, 0, 0, 0);
            grade.setSemester(cal.getTime());
            grade.setGrade('C');
            registrar.completeCourse(student, grade);
        }        
        List<Student> students = registrar.getStudents(0, 100);
        assertEquals("unexpected number of students:" + students.size(),
                1, students.size());
        assertEquals("unexpected number of courses:" + 
                students.get(0).getGrades().size(), 
                10, students.get(0).getGrades().size());
        List<Student> gradStudents = registrar.getGraduatingStudents(0, 100);
        assertEquals("unexpected number of graduating students:" + 
                gradStudents.size(),
                0, gradStudents.size());

        //this should get them graduated
        for(int i=0; i<10; i++) {
            Grade grade = new Grade();
            grade.setCourse("605.00" + i);
            cal.set(2000 + i, 2, 0, 0, 0);
            grade.setSemester(cal.getTime());
            grade.setGrade('B');
            registrar.completeCourse(student, grade);
        }        
        students = registrar.getStudents(0, 100);
        assertEquals("unexpected number of students:" + students.size(),
                1, students.size());
        assertEquals("unexpected number of courses:" + 
                students.get(0).getGrades().size(), 
                20, students.get(0).getGrades().size());
        gradStudents = registrar.getGraduatingStudents(0, 100);
        assertEquals("unexpected number of graduating students:" + 
                gradStudents.size(),
                1, gradStudents.size());
    
    }
    
    public void testDropStudent() throws RegistrarException {
        log.info("*** testDropStudent ***");
        Calendar cal = new GregorianCalendar();

        Student student = new Student();
        student.setFirstName("cat");
        student.setLastName("inhat");
        registrar.addStudent(student);
        for(int i=0; i<5; i++) {
            Grade grade = new Grade();
            grade.setCourse("605.00" + i);
            cal.set(2000 + i, 2, 0, 0, 0);
            grade.setSemester(cal.getTime());
            grade.setGrade('D');
            registrar.completeCourse(student, grade);
        }        
        List<Student> students = registrar.getStudents(0, 100);
        assertEquals("unexpected number of students:" + students.size(),
                1, students.size());

        registrar.dropStudent(student);
        students = registrar.getStudents(0, 100);
        assertEquals("unexpected number of students:" + students.size(),
                0, students.size());
    }
    
    public void testGetStudent() throws RegistrarException {
        log.info("*** testDropStudent ***");
        Calendar cal = new GregorianCalendar();

        Student student = new Student();
        student.setFirstName("cat");
        student.setLastName("inhat");
        registrar.addStudent(student);
        for(int i=0; i<5; i++) {
            Grade grade = new Grade();
            grade.setCourse("605.00" + i);
            cal.set(2000 + i, 2, 0, 0, 0);
            grade.setSemester(cal.getTime());
            grade.setGrade('D');
            registrar.completeCourse(student, grade);
        }        
        List<Student> students = registrar.getStudents(0, 100);
        assertEquals("unexpected number of students:" + students.size(),
                1, students.size());
        
        Student student2 = registrar.getStudent(student.getId());
        assertNotNull("student not gotten", student2);
        
        registrar.dropStudent(student);
        JPAUtil.getEntityManager().flush();
        JPAUtil.getEntityManager().clear();
        JPAUtil.getEntityManager().getTransaction().commit();
        JPAUtil.getEntityManager().getTransaction().begin();
        
        students = registrar.getStudents(0, 100);
        assertEquals("unexpected number of students:" + students.size(),
                0, students.size());
        
        Student student3 = registrar.getStudent(student.getId());
        assertNull("unexpected student gotten", student3);                
    }    
}
