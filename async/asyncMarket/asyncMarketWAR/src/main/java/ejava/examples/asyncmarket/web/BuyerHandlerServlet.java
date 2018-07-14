package ejava.examples.asyncmarket.web;

import java.io.IOException;

import java.util.HashMap;
import java.util.List;
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

import ejava.examples.asyncmarket.Buyer;
import ejava.examples.asyncmarket.UserMgmt;
import ejava.examples.asyncmarket.bo.AuctionItem;
import ejava.examples.asyncmarket.bo.Order;
import ejava.examples.asyncmarket.bo.Person;
import ejava.examples.asyncmarket.ejb.BuyerLocal;
import ejava.examples.asyncmarket.ejb.UserMgmtLocal;

@SuppressWarnings("serial")
public class BuyerHandlerServlet extends HttpServlet {
    private static Log log = LogFactory.getLog(BuyerHandlerServlet.class);
    private Map<String, Handler> handlers = new HashMap<String, Handler>();
    @EJB(beanInterface=BuyerLocal.class)
    private Buyer buyer;
    @EJB(beanInterface=UserMgmtLocal.class)
    private UserMgmt userMgmt;

    public static final String COMMAND_PARAM = "command";
    public static final String EXCEPTION_PARAM = "exception";
    public static final String HANDLER_TYPE_KEY = "type";
    public static final String ADMIN_TYPE = "admin";
    public static final String USER_TYPE = "user";
    public static final String MAINMENU_COMMAND = "menu";
    public static final String CREATEACCOUNT_COMMAND = "Create Account";        
    public static final String GET_AVAILABLE_ITEMS_COMMAND="Get Available Items";        
    public static final String CREATE_ORDER_COMMAND="Bid";        
    public static final String PLACE_ORDER_COMMAND="Place Order";        
    public static final String GET_ORDER_COMMAND="Get Order";        
    public static final String LOGOUT_COMMAND = "logout";        
    protected static final String DISPLAY_EXCEPTION_URL = 
        "/WEB-INF/content/DisplayException.jsp";
    private static final String UNKNOWN_COMMAND_URL = 
        "/WEB-INF/content/UnknownCommand.jsp";

    public void init() throws ServletException {
        log.debug("init() called, buyer=" + buyer + ", userMgmt=" + userMgmt);
        JNDIHelper jndi = null;
        try {
            //build a list of handlers for individual commands
            handlers.put(MAINMENU_COMMAND, new AdminMenu());
            handlers.put(CREATEACCOUNT_COMMAND, new CreateAccount());
            handlers.put(GET_AVAILABLE_ITEMS_COMMAND, new GetAvailableItems());
            handlers.put(CREATE_ORDER_COMMAND, new CreateOrder());
            handlers.put(PLACE_ORDER_COMMAND, new PlaceOrder());
            handlers.put(GET_ORDER_COMMAND, new GetOrder());
            handlers.put(LOGOUT_COMMAND, new Logout());

            //verify local injected or replace with remote
            ServletContext ctx = getServletContext();
            //TODO: jndi = new JNDIHelper(ctx);
            if (buyer == null) {
                buyer = jndi.getBuyer();
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
        protected static final String DISPLAYITEMS_URL = 
            "/WEB-INF/content/DisplayItems.jsp";
        protected static final String BUYERMENU_URL = 
            "/WEB-INF/content/BuyerMenu.jsp";
        protected static final String CREATEORDER_URL = 
            "/WEB-INF/content/CreateOrder.jsp";
        protected static final String DISPLAYORDER_URL = 
            "/WEB-INF/content/DisplayOrder.jsp";
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
                  getServletContext().getRequestDispatcher(BUYERMENU_URL);
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
    
    private class GetAvailableItems extends Handler {
        public void handle(HttpServletRequest request, 
                HttpServletResponse response) 
                throws ServletException, IOException {
            try {                
                List<AuctionItem> items = buyer.getAvailableItems(0, 100);
                request.setAttribute("items", items);
                
                RequestDispatcher rd = 
                  getServletContext().getRequestDispatcher(DISPLAYITEMS_URL);
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

    private class CreateOrder extends Handler {
        public void handle(HttpServletRequest request, 
                HttpServletResponse response) 
                throws ServletException, IOException {
            try {
                String itemIdString = request.getParameter("itemId");
                long itemId = Long.parseLong(itemIdString);
                AuctionItem item = buyer.getItem(itemId);
                request.setAttribute("item", item);
                
                RequestDispatcher rd = 
                  getServletContext().getRequestDispatcher(CREATEORDER_URL);
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

    private class PlaceOrder extends Handler {
        public void handle(HttpServletRequest request, 
                HttpServletResponse response) 
                throws ServletException, IOException {
            try {
                String itemIdString = request.getParameter("itemId");
                long itemId = Long.parseLong(itemIdString);
                String userId = request.getParameter("userId");
                String amountString = request.getParameter("maxAmount");
                double maxAmount = Double.parseDouble(amountString);
                
                log.debug("about to place order");
                long orderId = buyer.placeOrder(itemId, userId, maxAmount);
                Order order = buyer.getOrder(orderId);
                log.debug("order placed, displaying results");

                request.setAttribute("order", order);
                
                RequestDispatcher rd = 
                  getServletContext().getRequestDispatcher(DISPLAYORDER_URL);
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

    private class GetOrder extends Handler {
        public void handle(HttpServletRequest request, 
                HttpServletResponse response) 
                throws ServletException, IOException {
            try {
                String orderIdString = request.getParameter("orderId");
                long orderId = Long.parseLong(orderIdString);
                
                Order order = buyer.getOrder(orderId);

                request.setAttribute("order", order);
                
                RequestDispatcher rd = 
                  getServletContext().getRequestDispatcher(DISPLAYORDER_URL);
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
