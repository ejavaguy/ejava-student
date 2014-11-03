package ejava.examples.jndidemo.ejb;

import javax.annotation.Resource;
import javax.ejb.SessionContext;

/**
   This EJB is associated with an example to have the deployment leverage the
   XML deployment descriptors as much as possible.
*/

//@Stateless(name="Hospital") declared by ejb-jar.xml entry
public class HospitalEJB 
    extends SchedulerBase implements HospitalLocal, HospitalRemote {
    public String getName() { return "HospitalEJB"; }

    @Resource 
    public void setSessionContext(SessionContext ctx) { this.ctx = ctx; }
}
