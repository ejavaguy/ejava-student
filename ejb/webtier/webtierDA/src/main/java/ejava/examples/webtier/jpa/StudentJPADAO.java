package ejava.examples.webtier.jpa;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.webtier.bo.Student;
import ejava.examples.webtier.dao.StudentDAO;

public class StudentJPADAO implements StudentDAO {
    private Logger logger = LoggerFactory.getLogger(StudentJPADAO.class);
    private EntityManager em;

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
    
    public Student create(Student student) {
        em.persist(student);
        return student;
    }

    @SuppressWarnings("unchecked")
    public List<Student> find(int index, int count) {
        return em.createQuery(
                "select s from Student s")
        		       .setFirstResult(index)
                   .setMaxResults(count)
                   .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Student> find(
            String name, Map<String, Object> args, int index, int count) {
        logger.debug("named query:" + name + 
                ", index=" + index + ", count=" + count);
        Query query = em.createNamedQuery(name);
        if (query != null && args!=null) {
            for(Iterator<String> itr=args.keySet().iterator();
                itr.hasNext();) {
                String key = itr.next();
                Object value = args.get(key);
                query.setParameter(key, value);
                logger.debug("key=" + key + ", value=" + value);
            }
        }
        return query.setFirstResult(index)
                    .setMaxResults(count)
                    .getResultList();
    }

    public List<Student> find(String name, int index, int count) {
        return find(name, null, index, count);
    }

    public Student get(long id) {
        return em.find(Student.class, id);
    }

    public Student remove(Student student) {
            em.remove(student);
            return student;
    }

    public Student update(Student student) {
            return em.merge(student);
    }
}
