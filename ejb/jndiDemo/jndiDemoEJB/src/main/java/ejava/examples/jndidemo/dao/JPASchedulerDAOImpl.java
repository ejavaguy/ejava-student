package ejava.examples.jndidemo.dao;

import java.util.List;


import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.persistence.EntityManager;


import ejava.examples.jndidemo.JndiDemo;
import ejava.examples.jndidemo.bo.Task;

/**
 * This class represents a single JPA DAO implementation for tasks.
 */
@Typed({SchedulerDAO.class, JPASchedulerDAOImpl.class})
public class JPASchedulerDAOImpl extends JPADAOBase<Task> implements SchedulerDAO {
	
	@Inject 
	public void setEntityManager(@JndiDemo EntityManager em) {
		super.em = em;
	}
	
	public Task get(int id) {
		return em.find(Task.class, id);
	}

	@Override
	public Task findByName(String name) {
		List<Task> tasks = 
		em.createQuery("select t from Task t where t.name=:name", Task.class)
			.setParameter("name", name)
			.getResultList();
		return tasks.size()==0 ? null : tasks.get(0);
	}

	@Override
	public List<Task> getTasks(int offset, int limit) {
		return em.createQuery("select t from Task t", Task.class)
				.setFirstResult(offset)
				.setMaxResults(limit)
				.getResultList();
	}

	@Override
	public String toString() {
		return new StringBuilder(super.toString())
			.append(", em=" + em).toString();
	}
}
