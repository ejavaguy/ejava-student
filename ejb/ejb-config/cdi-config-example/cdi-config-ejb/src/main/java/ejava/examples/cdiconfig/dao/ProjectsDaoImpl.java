package ejava.examples.cdiconfig.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import ejava.examples.cdiconfig.CdiDemo;
import ejava.examples.cdiconfig.bo.Project;

public class ProjectsDaoImpl implements ProjectsDao {
    private EntityManager em;
    
    @PersistenceContext @CdiDemo
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public void create(Project task) {
        em.persist(task);
    }
    
    @Override
    public Project getProject(int id) {
        return em.find(Project.class, id);
    }

    @Override
    public List<Project> getProjects(int offset, int limit) {
        TypedQuery<Project> query = em.createQuery("select t from Project t", Project.class);
        if (offset>0) { query.setFirstResult(offset); }
        if (limit>0) { query.setMaxResults(limit); }
        return query.getResultList();
    }

    @Override
    public void delete(Project task) {
        em.remove(task);
    }
}
