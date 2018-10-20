package ejava.examples.cdiconfig.ejb;

import ejava.examples.cdiconfig.bo.Task;

public interface TasksMgmtRemote {
    int createTask(Task task);
}
