package ejava.examples.cdiconfig.dao;

import java.util.List;

import ejava.examples.cdiconfig.bo.Task;

public interface TasksDao {
    void create(Task task);
    Task getTask(int id);    
    List<Task> getTasks(int offset, int limit);
    void delete(Task task);
}
