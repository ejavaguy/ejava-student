package ejava.examples.webtier.dao;

public interface DAOTypeFactory {
    String getName();
    StudentDAO getStudentDAO();
}
