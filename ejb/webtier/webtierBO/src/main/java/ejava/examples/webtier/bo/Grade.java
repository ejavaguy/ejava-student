package ejava.examples.webtier.bo;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Grade implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private String course;
    private Date semester;
    private char grade;

    public Grade() {}
    public Grade(long id) {
        setId(id);
    }
    public Grade(long id, String course, Date semester, char grade) {
        setId(id);
        setCourse(course);
        setSemester(semester);
        setGrade(grade);
    }
    
    public long getId() {
        return id;
    }
    private void setId(long id) {
        this.id = id;
    }    
    public String getCourse() {
        return course;
    }
    public void setCourse(String course) {
        this.course = course;
    }
    public char getGrade() {
        return grade;
    }
    public void setGrade(char grade) {
        this.grade = grade;
    }
    public Date getSemester() {
        return semester;
    }
    public void setSemester(Date semester) {
        this.semester = semester;
    }
    
    public String toString() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(semester);
        StringBuilder text = new StringBuilder();
        text.append("id=" + id);
        text.append(", course=" + course);
        text.append(", semester=" + cal.get(Calendar.MONTH) + "-" +
                cal.get(Calendar.YEAR));
        text.append(", grade=" + grade);
        return text.toString();
    }

}
