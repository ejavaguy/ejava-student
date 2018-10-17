package ejava.examples.cdiconfig.ejb;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;

import ejava.examples.cdiconfig.bo.Project;
import ejava.examples.cdiconfig.dao.ProjectsDao;

@Stateless
@Remote(ProjectsMgmtRemote.class)
public class ProjectsMgmtEJB implements ProjectsMgmtRemote {
    @Inject
    private ProjectsDao projectsDao;
    
    @Override
    public int createProject(Project task) {
        projectsDao.create(task);
        return task.getId();
    }
}
