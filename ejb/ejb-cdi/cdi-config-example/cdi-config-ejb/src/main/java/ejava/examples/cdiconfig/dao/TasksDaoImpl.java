package ejava.examples.cdiconfig.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import ejava.examples.cdiconfig.bo.Task;

public class TasksDaoImpl implements TasksDao {
    private EntityManager em;
    
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public void create(Task task) {
        em.persist(task);
    }
    
    @Override
    public Task getTask(int id) {
        return em.find(Task.class, id);
    }

    @Override
    public List<Task> getTasks(int offset, int limit) {
        TypedQuery<Task> query = em.createQuery("select t from Task t", Task.class);
        if (offset>0) { query.setFirstResult(offset); }
        if (limit>0) { query.setMaxResults(limit); }
        return query.getResultList();
    }

    @Override
    public void delete(Task task) {
        em.remove(task);
    }

}
