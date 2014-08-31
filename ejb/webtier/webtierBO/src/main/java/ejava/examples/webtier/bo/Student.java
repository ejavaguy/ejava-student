package ejava.examples.webtier.bo;

import java.util.ArrayList;
import java.util.List;

public class Student extends Person {
    private static final long serialVersionUID = 1L;
    private List<Grade> grades = new ArrayList<Grade>();
    
    public Student() {}
    public Student(long id) { super(id); }
    public Student(
            long id, String firstName, String lastName, List<Grade> grades) {
        super(id, firstName, lastName);
        setGrades(grades);
    }

    public List<Grade> getGrades() {
        return grades;
    }
    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }
    
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append(super.toString());
        text.append(", grades=" + grades);
        return text.toString();
    }    
}
