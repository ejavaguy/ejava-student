package ejava.examples.cdiconfig.ejb;

import javax.annotation.PostConstruct;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ejava.examples.cdiconfig.bo.Task;
import ejava.examples.cdiconfig.dao.TasksDao;
import ejava.examples.cdiconfig.dao.TasksDaoImpl;

@Stateless
@Remote(TasksMgmtRemote.class)
public class TasksMgmtEJB implements TasksMgmtRemote {
    @PersistenceContext(unitName="cdi-config")
    private EntityManager em;
    
    private TasksDao taskDao;
    
    @PostConstruct
    public void init() {
        taskDao = new TasksDaoImpl();
        ((TasksDaoImpl)taskDao).setEntityManager(em);
    }
    
    @Override
    public int createTask(Task task) {
        taskDao.create(task);
        return task.getId();
    }
}
