package ejava.examples.cdiconfig.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import ejava.examples.cdiconfig.bo.Job;

public class JobsDaoImpl implements JobsDao {
    private EntityManager em;
    
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public void create(Job job) {
        em.persist(job);
    }
    
    @Override
    public Job getJob(int id) {
        return em.find(Job.class, id);
    }

    @Override
    public List<Job> getJobs(int offset, int limit) {
        TypedQuery<Job> query = em.createQuery("select t from Job t", Job.class);
        if (offset>0) { query.setFirstResult(offset); }
        if (limit>0) { query.setMaxResults(limit); }
        return query.getResultList();
    }

    @Override
    public void delete(Job job) {
        em.remove(job);
    }

}
