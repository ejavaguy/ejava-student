package ejava.examples.asyncmarket.web;

import java.io.IOException;
import java.util.Calendar;
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

import ejava.examples.asyncmarket.Seller;
import ejava.examples.asyncmarket.UserMgmt;
import ejava.examples.asyncmarket.bo.AuctionItem;
import ejava.examples.asyncmarket.bo.Person;
import ejava.examples.asyncmarket.ejb.SellerLocal;
import ejava.examples.asyncmarket.ejb.UserMgmtLocal;

@SuppressWarnings("serial")
public class SellerHandlerServlet extends HttpServlet {
    private static Log log = LogFactory.getLog(SellerHandlerServlet.class);
    private Map<String, Handler> handlers = new HashMap<String, Handler>();
    @EJB(beanInterface=SellerLocal.class)
    private Seller seller;
    @EJB(beanInterface=UserMgmtLocal.class)
    private UserMgmt userMgmt;

    public static final String COMMAND_PARAM = "command";
    public static final String EXCEPTION_PARAM = "exception";
    public static final String HANDLER_TYPE_KEY = "type";
    public static final String ADMIN_TYPE = "admin";
    public static final String USER_TYPE = "user";
    public static final String MAINMENU_COMMAND = "menu";
    public static final String CREATEACCOUNT_COMMAND = "Create Account";        
    public static final String SELLPRODUCT_COMMAND = "Sell Product";        
    public static final String GETITEMS_COMMAND = "Get Items";        
    public static final String LOGOUT_COMMAND = "logout";        
    protected static final String DISPLAY_EXCEPTION_URL = 
        "/WEB-INF/content/DisplayException.jsp";
    private static final String UNKNOWN_COMMAND_URL = 
        "/WEB-INF/content/UnknownCommand.jsp";

    public void init() throws ServletException {
        log.debug("init() called, seller=" + seller + ", userMgmt=" + userMgmt);
        JNDIHelper jndi = null;;
        try {
            //build a list of handlers for individual commands
            handlers.put(MAINMENU_COMMAND, new AdminMenu());
            handlers.put(CREATEACCOUNT_COMMAND, new CreateAccount());
            handlers.put(SELLPRODUCT_COMMAND, new SellProduct());
            handlers.put(GETITEMS_COMMAND, new GetItems());
            handlers.put(LOGOUT_COMMAND, new Logout());

            //verify local injected or replace with remote
            ServletContext ctx = getServletContext();
            //TODO: jndi = new JNDIHelper(ctx);
            if (seller == null) {
                seller = jndi.getSeller();
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
        	if (jndi != null) {
        		jndi.close();
        	}
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
                    log.debug("handler=" + handler);
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
        protected static final String DISPLAYUSER_URL = 
            "/WEB-INF/content/DisplayUser.jsp";
        protected static final String DISPLAYITEM_URL = 
            "/WEB-INF/content/DisplayItem.jsp";
        protected static final String SELLERMENU_URL = 
            "/WEB-INF/content/SellerMenu.jsp";
        protected static final String ITEM = "item";
        protected static final String USER = "user";
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
                  getServletContext().getRequestDispatcher(SELLERMENU_URL);
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

    private class CreateAccount extends Handler {
        public void handle(HttpServletRequest request, 
                HttpServletResponse response) 
                throws ServletException, IOException {
            try {
                String name = request.getParameter("name");
                String userId = request.getParameter("userId");
                
                Person user = new Person();
                user.setName(name);
                user.setUserId(userId);
                
                userMgmt.createUser(userId, name);
                user = userMgmt.getUserByUserId(userId);
                request.setAttribute(USER, user);
                
                RequestDispatcher rd = 
                  getServletContext().getRequestDispatcher(DISPLAYUSER_URL);
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
    
    private class SellProduct extends Handler {
        public void handle(HttpServletRequest request, 
                HttpServletResponse response) 
                throws ServletException, IOException {
            try {
                log.debug("in sell product");
                String name = request.getParameter("name");
                String delayString = request.getParameter("delay");
                int delay = Integer.parseInt(delayString);
                String minBidString = request.getParameter("midBid");
                double minBid = Double.parseDouble(minBidString);
                String sellerId = request.getParameter("userId");
                
                AuctionItem item = new AuctionItem();
                item.setName(name);
                item.setMinBid(minBid);
                Calendar cal = Calendar.getInstance();
                item.setStartDate(cal.getTime());
                cal.add(Calendar.SECOND, delay);
                item.setEndDate(cal.getTime());
                
                log.debug("calling EJB");
                long itemId = seller.sellProduct(sellerId, item);
                item = seller.getItem(itemId);
                request.setAttribute(ITEM, item);
                log.debug("about to forward");
                
                RequestDispatcher rd = 
                  getServletContext().getRequestDispatcher(DISPLAYITEM_URL);
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

    private class GetItems extends Handler {
        public void handle(HttpServletRequest request, 
                HttpServletResponse response) 
                throws ServletException, IOException {
            try {
                String userId = request.getParameter("userId");
                
                Person user = userMgmt.getUserByUserId(userId);
                
                request.setAttribute(USER, user);
                
                RequestDispatcher rd = 
                  getServletContext().getRequestDispatcher(DISPLAYUSER_URL);
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
