package ejava.examples.jndidemo.ejb;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

@Stateless
public class CookEJB extends SchedulerBase implements CookLocal {

    public String getName() { return "CookEJB"; }

    @Resource
    protected void setSessionContext(SessionContext ctx) {
        super.ctx = ctx;
    }
}
