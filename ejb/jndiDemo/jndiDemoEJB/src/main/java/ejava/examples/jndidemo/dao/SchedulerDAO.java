package ejava.examples.jndidemo.dao;

import java.util.List;

import ejava.examples.jndidemo.bo.Task;

/**
 * This interface represents a basic DAO type that manages tasks.
 */
public interface SchedulerDAO {
	void create(Task task);
	Task get(int id);
	Task update(Task task);
	void delete(Task task);
	Task findByName(String name);
	List<Task> getTasks(int offset, int limit);
}
