package ejava.examples.webtier.jpa;

import ejava.examples.webtier.dao.DAOFactory;
import ejava.examples.webtier.dao.DAOTypeFactory;
import ejava.examples.webtier.dao.StudentDAO;

public class JPADAOTypeFactory implements DAOTypeFactory {
    public static final String NAME = "JPA"; 
    static {
        DAOFactory.registerFactoryType(NAME, new JPADAOTypeFactory());
    }

    public String getName() { return NAME; }

    public StudentDAO getStudentDAO() {
        return new StudentJPADAO();
    }

}
