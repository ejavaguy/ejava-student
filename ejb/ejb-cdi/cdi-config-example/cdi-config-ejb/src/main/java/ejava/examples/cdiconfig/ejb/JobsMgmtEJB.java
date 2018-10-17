package ejava.examples.cdiconfig.ejb;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;

import ejava.examples.cdiconfig.CdiDemo;
import ejava.examples.cdiconfig.bo.Job;
import ejava.examples.cdiconfig.dao.JobsDao;

@Stateless
@Remote(JobsMgmtRemote.class)
public class JobsMgmtEJB implements JobsMgmtRemote {
    @Inject @CdiDemo
    private JobsDao jobDao;
    
    @Override
    public int createJob(Job job) {
        jobDao.create(job);
        return job.getId();
    }
}
