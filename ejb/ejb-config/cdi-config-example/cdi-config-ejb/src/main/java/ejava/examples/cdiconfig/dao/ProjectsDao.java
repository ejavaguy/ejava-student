package ejava.examples.cdiconfig.dao;

import java.util.List;

import ejava.examples.cdiconfig.bo.Project;

public interface ProjectsDao {
    void create(Project task);
    Project getProject(int id);    
    List<Project> getProjects(int offset, int limit);
    void delete(Project task);
}
