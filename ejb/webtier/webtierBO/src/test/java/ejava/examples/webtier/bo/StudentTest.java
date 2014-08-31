package ejava.examples.webtier.bo;

import static org.junit.Assert.*;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import ejava.examples.webtier.bo.Grade;
import ejava.examples.webtier.bo.Student;

public class StudentTest {
    Log log = LogFactory.getLog(StudentTest.class);

    @Test
    public void testGrade() {
        log.info("*** testGrade ***");
        
        Grade grade = new Grade();
        assertTrue("id assigned", grade.getId()==0);
        assertNull("course not null", grade.getCourse());
        assertNull("semester not null", grade.getSemester());
        assertEquals("grade assigned", 0, grade.getGrade());
        
        long id=101;
        String course = "605.000";        
        Calendar cal = new GregorianCalendar();
        char gradeVal = 'b';
        cal.set(2006, 9, 0, 0, 0);
        grade = new Grade(id);
        grade.setCourse(course);
        grade.setSemester(cal.getTime());
        grade.setGrade(gradeVal);
        assertEquals("unexpected id:" + grade.getId(), id, grade.getId());
        assertEquals("unexpected course" + grade.getCourse(), 
                course, grade.getCourse());
        assertEquals("unexpected semester:" + grade.getSemester(), 
                cal.getTime().getTime(), grade.getSemester().getTime());
        assertEquals("unexpected grade value:" + grade.getGrade(), 
                gradeVal, grade.getGrade());
        
        grade = new Grade(
                id, course, cal.getTime(), gradeVal);
        assertEquals("unexpected id:" + grade.getId(), id, grade.getId());
        assertEquals("unexpected course" + grade.getCourse(), 
                course, grade.getCourse());
        assertEquals("unexpected semester:" + grade.getSemester(), 
                cal.getTime().getTime(), grade.getSemester().getTime());
        assertEquals("unexpected grade value:" + grade.getGrade(), 
                gradeVal, grade.getGrade());
    }
    
    @Test
    public void testStudent() {
        log.info("*** testStudent ***");
        
        Student student = new Student();
        assertEquals("id assigned", 0, student.getId());
        assertNull("first name not null", student.getFirstName());
        assertNull("last name not null", student.getLastName());
        assertNotNull("grades collection is null", student.getGrades());
        
        Calendar cal = new GregorianCalendar();
        cal.set(2006,9,0,0,0);
        long id=301;
        String firstName = "manny";
        String lastName = "pep";
        List<Grade> grades = new ArrayList<Grade>();
        grades.add(new Grade(1,"605.001", cal.getTime(), 'A'));
        grades.add(new Grade(2,"605.002", cal.getTime(), 'B'));
        student = new Student(id);
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setGrades(grades);
        
        student = new Student(id, firstName, lastName, grades);
        assertEquals("unexpected id:" + student.getId(), id, student.getId());
        assertEquals("unexpected first name:" + student.getFirstName(), 
                firstName, student.getFirstName());        
        assertEquals("unexpected last name:" + student.getLastName(), 
                lastName, student.getLastName());
        assertNotNull("grades were null", student.getGrades());
        assertEquals("unexpected number of grades:" + student.getGrades().size(),
                grades.size(), student.getGrades().size());
    }
}
