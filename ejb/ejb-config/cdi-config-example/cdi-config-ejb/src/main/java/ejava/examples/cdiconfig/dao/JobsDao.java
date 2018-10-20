package ejava.examples.cdiconfig.dao;

import java.util.List;

import ejava.examples.cdiconfig.bo.Job;

public interface JobsDao {
    void create(Job job);
    Job getJob(int id);    
    List<Job> getJobs(int offset, int limit);
    void delete(Job job);
}
