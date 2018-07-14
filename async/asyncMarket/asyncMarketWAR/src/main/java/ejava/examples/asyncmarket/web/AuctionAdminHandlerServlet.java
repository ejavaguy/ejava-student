package ejava.examples.asyncmarket.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.asyncmarket.AuctionMgmt;
import ejava.examples.asyncmarket.UserMgmt;
import ejava.examples.asyncmarket.ejb.AuctionMgmtLocal;
import ejava.examples.asyncmarket.ejb.UserMgmtLocal;

@SuppressWarnings("serial")
public class AuctionAdminHandlerServlet extends HttpServlet {
    private static Log log = LogFactory.getLog(AuctionAdminHandlerServlet.class);
    private Map<String, Handler> handlers = new HashMap<String, Handler>();
    @EJB(beanInterface=AuctionMgmtLocal.class)
    private AuctionMgmt auctionMgmt;
    @EJB(beanInterface=UserMgmtLocal.class)
    private UserMgmt userMgmt;

    public static final String COMMAND_PARAM = "command";
    public static final String EXCEPTION_PARAM = "exception";
    public static final String HANDLER_TYPE_KEY = "type";
    public static final String ADMIN_TYPE = "admin";
    public static final String USER_TYPE = "user";
    public static final String MAINMENU_COMMAND = "menu";
    public static final String CANCELTIMERS_COMMAND = "Cancel Timers";        
    public static final String INITTIMERS_COMMAND = "Init Timers";        
    public static final String REMOVEACCOUNT_COMMAND = "Remove Account";        
    public static final String LOGOUT_COMMAND = "logout";        
    protected static final String DISPLAY_EXCEPTION_URL = 
        "/WEB-INF/content/DisplayException.jsp";
    private static final String UNKNOWN_COMMAND_URL = 
        "/WEB-INF/content/UnknownCommand.jsp";

    
    public void init() throws ServletException {
        log.debug("init() called, auctionMgmt=" + auctionMgmt + ", userMgmt=" + userMgmt);
        JNDIHelper jndi = null;
        try {
            //build a list of handlers for individual commands
            handlers.put(MAINMENU_COMMAND, new AdminMenu());
            handlers.put(CANCELTIMERS_COMMAND, new CancelTimers());
            handlers.put(INITTIMERS_COMMAND, new InitTimers());
            handlers.put(REMOVEACCOUNT_COMMAND, new RemoveAccount());
            handlers.put(LOGOUT_COMMAND, new Logout());

            //verify local references were injected or replace with remote
            ServletContext ctx = getServletContext();
            //TODO: jndi = new JNDIHelper(ctx);
            if (auctionMgmt == null) {
                auctionMgmt = jndi.getAuctionMgmt();
            }        
            if (userMgmt == null) {
                userMgmt = jndi.getUserMgmt();
            }        
        }
        catch (Exception ex) {
            log.fatal("error initializing handler", ex);
            throw new ServletException("error initializing handler", ex);
        }
        finally {
        	if (jndi != null) { jndi.close(); }
        }
    }

    protected void doGet(HttpServletRequest request, 
                         HttpServletResponse response) 
        throws ServletException, IOException {
        log.debug("doGet() called");
        String command = request.getParameter(COMMAND_PARAM);
        log.debug("command=" + command);
        try {            
            if (command != null) {
                Handler handler = handlers.get(command);
                if (handler != null) {
                    handler.handle(request, response);
                }
                else {
                    request.setAttribute("handlers", handlers);
                    RequestDispatcher rd = 
                        getServletContext().getRequestDispatcher(
                            UNKNOWN_COMMAND_URL);
                            rd.forward(request, response);
                }
            }
            else {
                throw new Exception("no " + COMMAND_PARAM + " supplied"); 
            }
        }
        catch (Exception ex) {
            request.setAttribute(EXCEPTION_PARAM, ex);
            RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    UNKNOWN_COMMAND_URL);
                    rd.forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, 
                          HttpServletResponse response) 
        throws ServletException, IOException {
        log.debug("doPost() called, calling doGet()");
        doGet(request, response);
    }

    public void destroy() {
        log.debug("destroy() called");
    }
    
    private abstract class Handler {
        protected static final String RESULT_PARAM = "result";
        protected static final String MAINMENU_URL = 
            "/index.jsp";
        protected static final String ADMINMENU_URL = 
            "/WEB-INF/content/AdminMenu.jsp";
        public abstract void handle(HttpServletRequest request, 
                HttpServletResponse response) 
                throws ServletException, IOException;        
    }
    
    private class AdminMenu extends Handler {
        public void handle(HttpServletRequest request, 
                HttpServletResponse response) 
                throws ServletException, IOException {
            try {
                RequestDispatcher rd = 
                  getServletContext().getRequestDispatcher(ADMINMENU_URL);
                rd.forward(request, response);                
            }
            catch (Exception ex) {
                request.setAttribute(EXCEPTION_PARAM, ex);
                RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    DISPLAY_EXCEPTION_URL);
                rd.forward(request, response);
            }
        }
    }

    private class CancelTimers extends Handler {
        public void handle(HttpServletRequest request, 
                HttpServletResponse response) 
                throws ServletException, IOException {
            try {
                auctionMgmt.cancelTimers();
                
                RequestDispatcher rd = 
                  getServletContext().getRequestDispatcher(ADMINMENU_URL);
                rd.forward(request, response);                
            }
            catch (Exception ex) {
                request.setAttribute(EXCEPTION_PARAM, ex);
                RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    DISPLAY_EXCEPTION_URL);
                rd.forward(request, response);
            }
        }
    }

    private class InitTimers extends Handler {
        public void handle(HttpServletRequest request, 
                HttpServletResponse response) 
                throws ServletException, IOException {
            try {
                String checkIntervalTimerStr = 
                    request.getParameter("checkIntervalTimer");
                long checkIntervalTimer = 
                    Long.parseLong(checkIntervalTimerStr);
                auctionMgmt.initTimers(checkIntervalTimer);
                
                RequestDispatcher rd = 
                  getServletContext().getRequestDispatcher(ADMINMENU_URL);
                rd.forward(request, response);                
            }
            catch (Exception ex) {
                request.setAttribute(EXCEPTION_PARAM, ex);
                RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    DISPLAY_EXCEPTION_URL);
                rd.forward(request, response);
            }
        }
    }

    private class RemoveAccount extends Handler {
        public void handle(HttpServletRequest request, 
                HttpServletResponse response) 
                throws ServletException, IOException {
            try {
                String userId = request.getParameter("userId");

                userMgmt.removeUser(userId);
                
                RequestDispatcher rd = 
                  getServletContext().getRequestDispatcher(ADMINMENU_URL);
                rd.forward(request, response);                
            }
            catch (Exception ex) {
                request.setAttribute(EXCEPTION_PARAM, ex);
                RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    DISPLAY_EXCEPTION_URL);
                rd.forward(request, response);
            }
        }
    }

    private class Logout extends Handler {
        public void handle(HttpServletRequest request, 
                HttpServletResponse response) 
                throws ServletException, IOException {
            try {
                request.getSession().invalidate();
                
                RequestDispatcher rd = 
                  getServletContext().getRequestDispatcher(MAINMENU_URL);
                rd.forward(request, response);                
            }
            catch (Exception ex) {
                request.setAttribute(EXCEPTION_PARAM, ex);
                RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    DISPLAY_EXCEPTION_URL);
                rd.forward(request, response);
            }
        }
    }
}
