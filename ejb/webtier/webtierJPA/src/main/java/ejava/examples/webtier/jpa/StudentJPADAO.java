package ejava.examples.webtier.jpa;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.webtier.bo.Student;
import ejava.examples.webtier.dao.StudentDAO;
import ejava.examples.webtier.dao.StudentDAOException;

public class StudentJPADAO implements StudentDAO {
    Log log = LogFactory.getLog(StudentJPADAO.class);

    public Student create(Student student) throws StudentDAOException {
        try {
            JPAUtil.getEntityManager().persist(student);
        }
        catch (Throwable ex) {
            throw new StudentDAOException(ex);
        }
        return student;
    }

    @SuppressWarnings("unchecked")
    public List<Student> find(int index, int count) throws StudentDAOException {
        try {
        return JPAUtil.getEntityManager().createQuery(
                "select s from Student s " +
                "").setFirstResult(index)
                   .setMaxResults(count)
                   .getResultList();
        }
        catch (Throwable ex) {
            throw new StudentDAOException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Student> find(
            String name, Map<String, Object> args, int index, int count) 
        throws StudentDAOException {
        try {
            log.debug("named query:" + name + 
                    ", index=" + index + ", count=" + count);
            Query query = JPAUtil.getEntityManager().createNamedQuery(name);
            if (query != null && args!=null) {
                for(Iterator<String> itr=args.keySet().iterator();
                    itr.hasNext();) {
                    String key = itr.next();
                    Object value = args.get(key);
                    query.setParameter(key, value);
                    log.debug("key=" + key + ", value=" + value);
                }
            }
            return query.setFirstResult(index)
                        .setMaxResults(count)
                        .getResultList();
        }
        catch (StudentDAOException ex) {
            throw new StudentDAOException(ex);
        }
    }

    public List<Student> find(String name, int index, int count) 
        throws StudentDAOException {
        return find(name, null, index, count);
    }

    public Student get(long id) throws StudentDAOException {
        try {
            return JPAUtil.getEntityManager().find(Student.class, id);
        }
        catch (StudentDAOException ex) {
            throw new StudentDAOException(ex);
        }
    }

    public Student remove(Student student) throws StudentDAOException {
        try {
            JPAUtil.getEntityManager().remove(student);
            return student;
        }
        catch (StudentDAOException ex) {
            throw new StudentDAOException(ex);
        }
    }

    public Student update(Student student) throws StudentDAOException {
        try {
            EntityManager em = JPAUtil.getEntityManager();
            return em.merge(student);
        }
        catch (StudentDAOException ex) {
            throw new StudentDAOException(ex);
        }
    }
}
