package ejava.examples.asyncmarket.web;

import javax.ejb.EJB;
import javax.ejb.ScheduleExpression;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.asyncmarket.AuctionMgmt;
import ejava.examples.asyncmarket.ejb.AuctionMgmtLocal;

@SuppressWarnings("serial")
public class InitServlet extends HttpServlet {
    private static Logger log = LoggerFactory.getLogger(InitServlet.class);
    @EJB(beanInterface=AuctionMgmtLocal.class)
    private AuctionMgmt auctionMgmt;

    public void init() throws ServletException {
        log.debug("init() called, auctionMgmt=" + auctionMgmt);
        try {
        	if (auctionMgmt == null) {
        		log.error("auctionMgmt is null, timers will not be initialized");
        	}
        	else {
        		log.debug("calling initTimers");
        		ScheduleExpression schedule = new ScheduleExpression();
        		schedule.second("*/10");
        		schedule.minute("*");
        		schedule.hour("*");
        		schedule.dayOfMonth("*");
        		schedule.month("*");
        		schedule.year("*");
        		auctionMgmt.initTimers(schedule);
        	}
        }
        catch (Exception ex) {
            log.fatal("error initializing", ex);
            throw new ServletException("error initializing", ex);
        }
    }
}
