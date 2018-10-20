package ejava.examples.cdiconfig.ejb;

import ejava.examples.cdiconfig.bo.Job;

public interface JobsMgmtRemote {
    int createJob(Job job);
}
