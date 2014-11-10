package info.ejava.examples.secureping.web;

import info.ejava.examples.secureping.ejb.SecurePing;
import info.ejava.examples.secureping.ejb.SecurePingLocal;
import info.ejava.examples.secureping.ejb.SecurePingRemote;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class SecurePingHandlerServlet extends HttpServlet {
    private static Logger logger = LoggerFactory.getLogger(SecurePingHandlerServlet.class);
    private Map<String, Handler> handlers = new HashMap<String, Handler>();
    
    @EJB(beanName="SecurePingEJB", beanInterface=SecurePingLocal.class)
    private SecurePing securePingServer;

    public static final String COMMAND_PARAM = "command";
    public static final String EXCEPTION_PARAM = "exception";
    public static final String HANDLER_TYPE_KEY = "type";
    public static final String ADMIN_TYPE = "admin";
    public static final String USER_TYPE = "user";
    public static final String MAINMENU_COMMAND = "menu";
    public static final String IS_CALLER_IN_ROLE_COMMAND = "isCallerInRole";
    public static final String PING_ALL_COMMAND = "pingAll";
    public static final String PING_USER_COMMAND = "pingUser";
    public static final String PING_ADMIN_COMMAND = "pingAdmin";
    public static final String PING_EXCLUDED_COMMAND = "pingExcluded";        
    public static final String LOGOUT_COMMAND = "logout";        
    protected static final String DISPLAY_EXCEPTION_URL = 
        "/WEB-INF/content/DisplayException.jsp";
    private static final String UNKNOWN_COMMAND_URL = 
        "/WEB-INF/content/UnknownCommand.jsp";

    public void init() throws ServletException {
        logger.debug("init() called ");
        try {
            ServletConfig config = getServletConfig();
            initServerRef(config);
            
            //build a list of handlers for individual commands
            //String handler = config.getInitParameter(HANDLER_TYPE_KEY);
            
            handlers.put(MAINMENU_COMMAND, new MainMenu());
            handlers.put(IS_CALLER_IN_ROLE_COMMAND, new IsCallerInRole());
            handlers.put(PING_ALL_COMMAND, new PingAll());
            handlers.put(PING_USER_COMMAND, new PingUser());
            handlers.put(PING_ADMIN_COMMAND, new PingAdmin());
            handlers.put(PING_EXCLUDED_COMMAND, new PingExcluded());            
            handlers.put(LOGOUT_COMMAND, new Logout());            
        }
        catch (Exception ex) {
            logger.error("error initializing handler", ex);
            throw new ServletException("error initializing handler", ex);
        }
    }

    @SuppressWarnings("rawtypes")
    private void initServerRef(ServletConfig config) throws Exception {        
        logger.debug("initServerRef(), securePingServer=" + securePingServer);
        if (securePingServer == null) {
            //build an InitialContext from Servlet.init properties in web.xml
            Properties jndiProperties = new Properties();
            for(Enumeration e=config.getInitParameterNames();
                e.hasMoreElements(); ) {
                String key = (String)e.nextElement();
                String value=(String)config.getInitParameter(key);
                if (key.startsWith("java.naming")) {
                    jndiProperties.put(key, value);
                }                    
            }
            logger.debug("jndiProperties=" + jndiProperties);
            InitialContext jndi = new InitialContext(jndiProperties);
            String jndiName = config.getInitParameter("registrar.local");
            try { securePingServer = (SecurePingLocal)jndi.lookup(jndiName); }
            catch (Throwable ex) {
                logger.debug(jndiName + " not found, trying remote");
                jndiName = config.getInitParameter("secureping.remote");
                securePingServer = (SecurePingRemote)jndi.lookup(jndiName);
            }
            logger.debug("server ref initialized:" + securePingServer);
        }        
    }

    protected void doGet(HttpServletRequest request, 
                         HttpServletResponse response) 
        throws ServletException, IOException {
        logger.debug("doGet() called");
        String command = request.getParameter(COMMAND_PARAM);
        logger.debug("command=" + command);
        try {            
            if (command != null) {
                Handler handler = handlers.get(command);
                if (handler != null) {
                    handler.handle(request, response);
                }
                else {
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
        logger.debug("doPost() called, calling doGet()");
        doGet(request, response);
    }

    public void destroy() {
        logger.debug("destroy() called");
    }
    
    private abstract class Handler {
        protected static final String ROLE_PARAM = "role";
        protected static final String RESULT_PARAM = "result";
        protected static final String MAINMENU_URL = 
            "/WEB-INF/content/MainMenu.jsp";
        protected static final String DISPLAY_RESULT_URL = 
            "/WEB-INF/content/DisplayResult.jsp";
        public abstract void handle(HttpServletRequest request, 
                HttpServletResponse response) 
                throws ServletException, IOException;        
    }
    
    private class MainMenu extends Handler {
        public void handle(HttpServletRequest request, 
                HttpServletResponse response) 
                throws ServletException, IOException {
            try {
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

    private class IsCallerInRole extends Handler {
        public void handle(HttpServletRequest request, 
                HttpServletResponse response) 
                throws ServletException, IOException {
            try {
                String role = 
                    (String)request.getParameter(ROLE_PARAM);                
                
                Boolean result = securePingServer.isCallerInRole(role);
                
                request.setAttribute(RESULT_PARAM, 
                        "isCallerInRole(" + role + ")=" + result);                
                RequestDispatcher rd = 
                  getServletContext().getRequestDispatcher(DISPLAY_RESULT_URL);
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
    
    private abstract class Ping extends Handler {
        protected abstract String doPing();
        public void handle(HttpServletRequest request, 
                HttpServletResponse response) 
                throws ServletException, IOException {
            try {
                String result = doPing();
                
                request.setAttribute(RESULT_PARAM, result);                
                RequestDispatcher rd = 
                  getServletContext().getRequestDispatcher(DISPLAY_RESULT_URL);
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
    
    private class PingAll extends Ping {
        protected String doPing() { return securePingServer.pingAll(); }
    }
    private class PingUser extends Ping {
        protected String doPing() { return securePingServer.pingUser(); }
    }
    private class PingAdmin extends Ping {
        protected String doPing() { return securePingServer.pingAdmin(); }
    }
    private class PingExcluded extends Ping {
        protected String doPing() { return securePingServer.pingExcluded(); }
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
