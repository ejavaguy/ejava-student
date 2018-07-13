package ejava.examples.webtier.jpa;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import ejava.examples.webtier.bo.Grade;
import ejava.examples.webtier.bo.Student;

public class JPADAOTest extends JPADAOTestBase {
	@Test
    public void testDAOCreate() {
        log.info("*** testDAOCreate ***");
        
        Student student = new Student();
        student.setFirstName("cat");
        student.setLastName("inhat");
        dao.create(student);        
    }
    
	@Test
    public void testDAOGet() {
        log.info("*** testDAOGet ***");
        Student student = new Student();
        student.setFirstName("cat");
        student.setLastName("inhat");
        dao.create(student);
        
        Student student2 = dao.get(student.getId());
        assertNotNull("failed to get student",student2);        
    }

	@Test
    public void testDAOFind() {
        log.info("*** testDAOGet ***");
        dao.create(new Student(0,"cat", "inhat", new ArrayList<Grade>()));
        dao.create(new Student(0,"thing", "one", new ArrayList<Grade>()));
        dao.create(new Student(0,"thing", "two", new ArrayList<Grade>()));

        List<Student> students = dao.find(0,100);
        assertNotNull("failed to find students",students);
        assertEquals("unexpected number of students:" + students.size(),
                3, students.size());
        
        for(int i=0; i<97; i++) {
            String firstName = "First";
            String lastName = "Last " + new Integer(i);
            dao.create(
                new Student(0,firstName, lastName, new ArrayList<Grade>()));
        }
        
        int count = dao.find(0,100).size();
        assertEquals("unexpected count:" + count, 100, count);
        count = dao.find(0,10).size();
        assertEquals("unexpected count:" + count, 10, count);
        count = dao.find(0,200).size();
        assertEquals("unexpected count:" + count, 100, count);
        int pageSize = 25;
        for(int page=0; page<(100/pageSize); page++) {
            int index = pageSize * page;
            count = dao.find(index,pageSize).size();
            assertEquals("unexpected count:" + count, pageSize, count);
            log.info("index=" + index + 
                    ", size=" + pageSize + 
                    ", count=" + count);
        }

    }
    
	@Test	
    public void testUpdate() {
        log.info("*** testUpdate ***");

        Student student = new Student(0,"cat", "inhat", new ArrayList<Grade>()); 
        dao.create(student);
        
        Student student2 = 
            new Student(student.getId(),"foo", "bar",new ArrayList<Grade>());
        Student student3 = dao.update(student2);
        
        assertNotNull("student is null", student3);
        assertEquals("unexpected last name:" + student3.getId(), 
                student.getId(), student3.getId());
        assertEquals("unexpected first name:" + student3.getFirstName(), 
                student2.getFirstName(), student3.getFirstName());
        assertEquals("unexpected last name:" + student3.getLastName(), 
                student2.getLastName(), student3.getLastName());
        assertEquals("unexpected grades:" + student3.getGrades().size(), 
                0, student3.getGrades().size());        
    }
    
	@Test
    public void testDAORemove() {
        log.info("*** testDAORemove ***");

        Student student = new Student(0,"cat", "inhat", new ArrayList<Grade>()); 
        long id = dao.create(student).getId();
        
        Student student2 = dao.get(id);
        assertNotNull("student not found", student2);
        
        dao.remove(student);
        boolean found=true;
        try {
            found = (dao.get(id) != null);
        }
        catch (Throwable ex) {
            found=false;            
        }
        finally {
            assertFalse("unexpected student found", found);
        }
    }
    
	@Test
	public void testDAONamedQuery() {
        log.info("*** testDAONamedQuery ***");

        dao.create(new Student(0,"cat", "inhat", new ArrayList<Grade>())); 
        dao.create(new Student(0,"thing", "one", new ArrayList<Grade>())); 
        dao.create(new Student(0,"thing", "two", new ArrayList<Grade>()));
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("firstName", "thing");
        params.put("lastName", "%");
        List<Student> students = dao.find("getStudentsByName", params, 0,100);
        
        assertNotNull("students null", students);
        assertEquals("unexpected number of students found:" + students.size(), 
                2, students.size());        
    }
    
	@Test
    public void testDAONamedQuery2() {
        log.info("*** testDAONamedQuery2 ***");

        dao.create(new Student(0,"cat", "inhat", new ArrayList<Grade>())); 
        dao.create(new Student(0,"thing", "one", new ArrayList<Grade>())); 
        dao.create(new Student(0,"thing", "two", new ArrayList<Grade>()));
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("firstName", "%");
        params.put("lastName", "%");
        List<Student> students = dao.find("getStudentsByName", params, 0,100);
        
        assertNotNull("students null", students);
        assertEquals("unexpected number of students found:" + students.size(), 
                3, students.size());        
    }

    
	@Test
    public void testNewSudents() {
        log.info("*** testNewStudents ***");
        Calendar cal = new GregorianCalendar();

        Student student1=new Student(0,"cat", "inhat", new ArrayList<Grade>()); 
        dao.create(student1);
        Student student2=new Student(0,"thing", "one", new ArrayList<Grade>()); 
        dao.create(student2);
        dao.create(new Student(0,"thing", "two", new ArrayList<Grade>())); 
        
        for(int i=0; i<10; i++) {
            cal.set(2000+i, 9, 0, 0, 0);
            student1.getGrades().add(
                new Grade(0, "605.00" + i, cal.getTime(), 'A'));
        }
        for(int i=0; i<4; i++) {
            cal.set(2000+i, 9, 0, 0, 0);
            student2.getGrades().add(
                new Grade(0, "605.01" + i, cal.getTime(), 'B'));
        }
        
        List<Student> students = 
            dao.find(0, 100);
        assertEquals("unexpected number of students:" + students.size(),
                3, students.size());

        List<Student> newStudents = 
            dao.find("getNewStudents", null, 0, 100);
        assertEquals("unexpected number of new students:" + newStudents.size(),
                1, newStudents.size());

        List<Student> gradStudents = 
            dao.find("getGraduatingStudents", null, 0, 100);
        assertEquals("unexpected number of grad students:" + gradStudents.size(),
                1, gradStudents.size());
    }


	@Test
    public void testGraduating() {
        log.info("*** testGraduating ***");
        Calendar cal = new GregorianCalendar();

        int students = dao.find("getGraduatingStudents", null, 0, 100).size();
        assertEquals("unexpected number of graduating:" + students, 0, students);
        
        Student student1=new Student(0,"cat", "inhat", new ArrayList<Grade>()); 
        dao.create(student1);
        
        students = dao.find("getGraduatingStudents", null, 0, 100).size();
        assertEquals("unexpected number of graduating:" + students, 0, students);
        
        //not enough to graduate
        for(int i=0; i<5; i++) {
            cal.set(2000+i, 9, 0, 0, 0);
            student1.getGrades().add(
                new Grade(0, "605.00" + i, cal.getTime(), 'A'));
            
        }
        students = dao.find("getGraduatingStudents", null, 0, 100).size();
        assertEquals("unexpected number of graduating:" + students, 0, students);        

        //no credit for C
        for(int i=0; i<11; i++) {
            cal.set(2000+i, 9, 0, 0, 0);
            student1.getGrades().add(
                new Grade(0, "605.01" + i, cal.getTime(), 'C'));
            
        }
        students = dao.find("getGraduatingStudents", null, 0, 100).size();
        assertEquals("unexpected number of graduating:" + students, 0, students);        

        //okay, here's enough
        for(int i=0; i<5; i++) {
            cal.set(2000+i, 9, 0, 0, 0);
            student1.getGrades().add(
                new Grade(0, "605.02" + i, cal.getTime(), 'A'));
            
        }
        students = dao.find("getGraduatingStudents", null, 0, 100).size();
        assertEquals("unexpected number of graduating:" + students, 1, students);        

    }

}
