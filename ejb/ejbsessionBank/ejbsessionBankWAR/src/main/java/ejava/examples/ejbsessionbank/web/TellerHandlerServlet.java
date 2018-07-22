package ejava.examples.ejbsessionbank.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.ejbsessionbank.bl.Teller;
import ejava.examples.ejbsessionbank.bo.Account;
import ejava.examples.ejbsessionbank.bo.Ledger;
import ejava.examples.ejbsessionbank.ejb.TellerLocal;
import ejava.examples.ejbsessionbank.ejb.TellerRemote;
import ejava.util.ejb.EJBClient;
import ejava.util.jndi.JNDIUtil;

@SuppressWarnings("serial")
public class TellerHandlerServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(TellerHandlerServlet.class);
    public static final String COMMAND_PARAM = "command";
    public static final String EXCEPTION_PARAM = "javax.servlet.error.exception";
    public static final String HANDLER_TYPE_KEY = "type";
    public static final String ADMIN_TYPE = "admin";
    public static final String CREATE_ACCOUNT_COMMAND = "Create Account";
    public static final String DEPOSIT_COMMAND = "Deposit";
    public static final String WITHDRAW_COMMAND = "Withdraw";
    public static final String GET_ACCOUNT_COMMAND = "Get Account";
    public static final String CLOSE_ACCOUNT_COMMAND = "Close Account";
    public static final String GET_ACCOUNTS_COMMAND = "Get Accounts";
    public static final String CREATE_ACCOUNTS_COMMAND = "Create Accounts";
    public static final String GET_LEDGER_COMMAND = "Get Ledger";
    public static final String STEAL_ALL_ACCOUNTS_COMMAND = "Steal All Accounts";
    public static final String jndiName = 
         EJBClient.getEJBClientLookupName("ejbsessionBankEAR", "ejbsessionBankEJB", "", "TellerEJB", TellerRemote.class.getName(), false);
        
    private static final String UNKNOWN_COMMAND_URL = 
        "/WEB-INF/content/UnknownCommand.jsp";
    private static final String ERROR_URL = 
            "/WEB-INF/content/ErrorPage.jsp";
    private Map<String, Handler> handlers = new HashMap<String, Handler>();
    
    /**
     * This will get automatically injected when running within the 
     * application server with the TellerEJB. beanInterface is only
     * needed to resolve the derived type ambiguity for Local and Remote
     * caused by the design of our example. 
     */
    @javax.ejb.EJB(beanInterface=TellerLocal.class)
    private Teller injectedTeller;
    private Teller teller;

    /**
     * Init verify the teller reference to the EJB logic is in place and
     * initializes the proper handler for the assigned role supplied in 
     * the servlet init parameters.
     */
    public void init() throws ServletException {
        logger.debug("init() called; teller=" + injectedTeller);
        
        try {
            ServletConfig config = getServletConfig();
            
            teller = injectedTeller!=null ? injectedTeller : getTeller();
            if (teller==null) {
                throw new Exception("no teller injected or found in JNDI lookup");
            }
            
            //build a list of handlers for individual commands
            if (ADMIN_TYPE.equals(config.getInitParameter(HANDLER_TYPE_KEY))) {
                handlers.put(CREATE_ACCOUNT_COMMAND, new CreateAccount());    
                handlers.put(GET_ACCOUNT_COMMAND, new GetAccount());    
                handlers.put(CLOSE_ACCOUNT_COMMAND, new CloseAccount());    
                handlers.put(DEPOSIT_COMMAND, new DepositAccount());    
                handlers.put(WITHDRAW_COMMAND, new WithdrawAccount());    
                handlers.put(GET_ACCOUNTS_COMMAND, new GetAccounts());    
                handlers.put(CREATE_ACCOUNTS_COMMAND, new CreateAccounts());    
                handlers.put(GET_LEDGER_COMMAND, new GetLedger());    
                handlers.put(STEAL_ALL_ACCOUNTS_COMMAND, new StealAccounts());    
            }            
        }
        catch (Exception ex) {
            logger.error("error initializing handler", ex);
            throw new ServletException("error initializing handler", ex);
        }
    }
    
    /**
     * This helper method will return a Teller in development based on a JNDI lookup.
     * @return teller if found
     * @throws NamingException
     * @throws IOException 
     */
    protected Teller getTeller() throws NamingException, IOException {
        Teller teller = null;
        InputStream is = null;
        InitialContext jndi = null;
        try {
            //manually load the JNDI properties to make sure we don't get a Jetty JNDI tree in dev
            if ((is=getClass().getResourceAsStream("/jndi.properties"))==null) {
                logger.warn("no jndi.properties found, check classpath");
            } else {
                Properties jndiProperties = new Properties();
                jndiProperties.load(is);
                logger.info("jndiProperties={}", jndiProperties);
    
                jndi = new InitialContext(jndiProperties);
                logger.debug("looking up: {}", jndiName);
                teller = (Teller)jndi.lookup(jndiName);
                logger.debug("found {}", teller);
            }
          } finally {
            if (is!=null) {
                try { is.close(); } catch(Exception ex) {}
            }
            if (jndi!=null) {
                try { jndi.close(); } catch(Exception ex) {}
            }
        }
        return teller;
    }

    /**
     * This is the main dispatch method for the servlet. It expects to
     * find a command keyed by an argument in the request parameters.
     */
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
                    handler.handle(request, response, teller);
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
        } catch (NamingException ex) {
            request.setAttribute(EXCEPTION_PARAM, ex);
            RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    ERROR_URL);
                    rd.forward(request, response);
        } catch (Exception ex) {
            request.setAttribute(EXCEPTION_PARAM, ex);
            RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    UNKNOWN_COMMAND_URL);
                    rd.forward(request, response);
        }
    }

    /**
     * Since this is a toy, we don't really care whether they call get or post.
     */
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
        protected static final String MAIN_PAGE = 
            "/index.jsp";
        protected static final String DISPLAY_EXCEPTION = 
            "/WEB-INF/content/DisplayException.jsp";
        protected static final String ACCT_NUM_PARAM = "accountNumber";
        protected static final String AMOUNT_PARAM = "amount";
        protected static final String INDEX_PARAM = "index";
        protected static final String NEXT_INDEX_PARAM = "nextIndex";
        protected static final String COUNT_PARAM = "count";
        protected static final String ACCOUNT_PARAM = "account";
        protected static final String ACCOUNTS_PARAM = "accounts";
        protected static final String LEDGER_PARAM = "ledger";
        protected static final String DISPLAY_ACCOUNT_URL = 
            "/WEB-INF/content/DisplayAccount.jsp";
        protected static final String DISPLAY_ACCOUNTS_URL = 
            "/WEB-INF/content/DisplayAccounts.jsp";
        protected static final String DISPLAY_LEDGER_URL = 
            "/WEB-INF/content/DisplayLedger.jsp";
        public abstract void handle(HttpServletRequest request, 
                HttpServletResponse response, Teller t) 
                throws ServletException, IOException;
    }
    
    private class CreateAccount extends Handler {
        public void handle(HttpServletRequest request, 
                HttpServletResponse response, Teller t) 
                throws ServletException, IOException {
            try {
                String acctNum = (String)request.getParameter(ACCT_NUM_PARAM);                
                Account account = t.createAccount(acctNum);
                
                request.setAttribute(ACCOUNT_PARAM, account);                
                RequestDispatcher rd = 
                  getServletContext().getRequestDispatcher(DISPLAY_ACCOUNT_URL);
                rd.forward(request, response);                
            }
            catch (Exception ex) {
                logger.error("error creating account:" + ex, ex);
                request.setAttribute(EXCEPTION_PARAM, ex);
                RequestDispatcher rd = 
                    getServletContext().getRequestDispatcher(DISPLAY_EXCEPTION);
                rd.forward(request, response);                
            }
        }
    }

    private class GetAccount extends Handler {
        public void handle(HttpServletRequest request, 
                HttpServletResponse response, Teller t) 
                throws ServletException, IOException {
            try {
                String acctNum = (String)request.getParameter(ACCT_NUM_PARAM);                
                Account account = t.getAccount(acctNum);
                
                request.setAttribute(ACCOUNT_PARAM, account);                
                RequestDispatcher rd = 
                  getServletContext().getRequestDispatcher(DISPLAY_ACCOUNT_URL);
                rd.forward(request, response);                
            }
            catch (Exception ex) {
                logger.error("error creating account:" + ex, ex);
                request.setAttribute(EXCEPTION_PARAM, ex);
                RequestDispatcher rd = 
                    getServletContext().getRequestDispatcher(DISPLAY_EXCEPTION);
                rd.forward(request, response);                
            }
        }
    }

    private class DepositAccount extends Handler {
        public void handle(HttpServletRequest request, 
                HttpServletResponse response, Teller t) 
                throws ServletException, IOException {
            try {
                String acctNum = (String)request.getParameter(ACCT_NUM_PARAM);                
                String amountStr = (String)request.getParameter(AMOUNT_PARAM);
                double amount = Double.parseDouble(amountStr);
                
                Account account = t.getAccount(acctNum);
                account.deposit(amount);
                t.updateAccount(account);
                
                request.setAttribute(ACCOUNT_PARAM, account);                
                RequestDispatcher rd = 
                  getServletContext().getRequestDispatcher(DISPLAY_ACCOUNT_URL);
                rd.forward(request, response);                
            }
            catch (Exception ex) {
                logger.error("error depositing to account:" + ex, ex);
                request.setAttribute(EXCEPTION_PARAM, ex);
                RequestDispatcher rd = 
                    getServletContext().getRequestDispatcher(DISPLAY_EXCEPTION);
                rd.forward(request, response);                
            }
        }
    }

    private class WithdrawAccount extends Handler {
        public void handle(HttpServletRequest request, 
                HttpServletResponse response, Teller t) 
                throws ServletException, IOException {
            try {
                String acctNum = (String)request.getParameter(ACCT_NUM_PARAM);                
                String amountStr = (String)request.getParameter(AMOUNT_PARAM);
                double amount = Double.parseDouble(amountStr);
                
                Account account = t.getAccount(acctNum);
                account.withdraw(amount);
                t.updateAccount(account);
                
                request.setAttribute(ACCOUNT_PARAM, account);                
                RequestDispatcher rd = 
                  getServletContext().getRequestDispatcher(DISPLAY_ACCOUNT_URL);
                rd.forward(request, response);                
            }
            catch (Exception ex) {
                logger.error("error withdrawing from account:" + ex, ex);
                request.setAttribute(EXCEPTION_PARAM, ex);
                RequestDispatcher rd = 
                    getServletContext().getRequestDispatcher(DISPLAY_EXCEPTION);
                rd.forward(request, response);                
            }
        }
    }

    private class CloseAccount extends Handler {
        public void handle(HttpServletRequest request, 
                HttpServletResponse response, Teller t) 
                throws ServletException, IOException {
            try {
                String acctNum = (String)request.getParameter(ACCT_NUM_PARAM);                
                
                t.closeAccount(acctNum);
                
                response.sendRedirect(request.getContextPath() + MAIN_PAGE);
            }
            catch (Exception ex) {
                logger.error("error closing account:" + ex, ex);
                request.setAttribute(EXCEPTION_PARAM, ex);
                RequestDispatcher rd = 
                    getServletContext().getRequestDispatcher(DISPLAY_EXCEPTION);
                rd.forward(request, response);                
            }
        }
    }

    private class CreateAccounts extends Handler {
        public void handle(HttpServletRequest request, 
                HttpServletResponse response, Teller t) 
                throws ServletException, IOException {
            try {
                String countStr = (String)request.getParameter(COUNT_PARAM);
                int count = Integer.parseInt(countStr);
                
                long seed = System.currentTimeMillis();
                for(int i=0; i<count; i++) {
                    Account account = t.createAccount("" + seed + "-" + i);
                    account.deposit(i);
                    t.updateAccount(account);                    
                }
                
                response.sendRedirect(request.getContextPath() + MAIN_PAGE);
            }
            catch (Exception ex) {
                logger.error("error closing account:" + ex, ex);
                request.setAttribute(EXCEPTION_PARAM, ex);
                RequestDispatcher rd = 
                    getServletContext().getRequestDispatcher(DISPLAY_EXCEPTION);
                rd.forward(request, response);                
            }
        }
    }

    private class GetAccounts extends Handler {
        public void handle(HttpServletRequest request, 
                HttpServletResponse response, Teller t) 
                throws ServletException, IOException {
            try {
                String indexStr = (String)request.getParameter(INDEX_PARAM);
                String countStr = (String)request.getParameter(COUNT_PARAM);
                int index = Integer.parseInt(indexStr);
                int count = Integer.parseInt(countStr);
                
                List<Account> accounts = t.getAccounts(index, count);
                
                int nextIndex = (accounts.size()==0) ? 
                        index : index + accounts.size();
                
                request.setAttribute(ACCOUNTS_PARAM, accounts);
                request.setAttribute(INDEX_PARAM, index);
                request.setAttribute(COUNT_PARAM, count);
                request.setAttribute(NEXT_INDEX_PARAM, nextIndex);
                
                RequestDispatcher rd = 
                 getServletContext().getRequestDispatcher(DISPLAY_ACCOUNTS_URL);
                rd.forward(request, response);                
            }
            catch (Exception ex) {
                logger.error("error getting accounts:" + ex, ex);
                request.setAttribute(EXCEPTION_PARAM, ex);
                RequestDispatcher rd = 
                    getServletContext().getRequestDispatcher(DISPLAY_EXCEPTION);
                rd.forward(request, response);                
            }
        }
    }

    private class GetLedger extends Handler {
        public void handle(HttpServletRequest request, 
                HttpServletResponse response, Teller t) 
                throws ServletException, IOException {
            try {
                Ledger ledger = t.getLedger();
                
                request.setAttribute(LEDGER_PARAM, ledger);
                
                RequestDispatcher rd = 
                   getServletContext().getRequestDispatcher(DISPLAY_LEDGER_URL);
                rd.forward(request, response);                
            }
            catch (Exception ex) {
                logger.error("error getting ledger:" + ex, ex);
                request.setAttribute(EXCEPTION_PARAM, ex);
                RequestDispatcher rd = 
                    getServletContext().getRequestDispatcher(DISPLAY_EXCEPTION);
                rd.forward(request, response);                
            }
        }
    }

    private class StealAccounts extends Handler {
        public void handle(HttpServletRequest request, 
                HttpServletResponse response, Teller t) 
                throws ServletException, IOException {
            try {
                List<Account> accounts = t.getAccounts(0, 100);
                while (accounts.size() > 0) {
                    logger.debug("closing " + accounts.size() + " accounts");
                    for (Account account : accounts) {
                        if (account.getBalance() > 0) {
                            account.withdraw(account.getBalance());
                            t.updateAccount(account);
                        }
                        else if (account.getBalance() < 0) {
                            account.deposit(account.getBalance() * -1);
                            t.updateAccount(account);
                        }
                        t.closeAccount(account.getAccountNumber());
                    }
                    accounts = t.getAccounts(0, 100);
                }
                
                response.sendRedirect(request.getContextPath() + MAIN_PAGE);
            }
            catch (Exception ex) {
                logger.error("error getting ledger:" + ex, ex);
                request.setAttribute(EXCEPTION_PARAM, ex);
                RequestDispatcher rd = 
                    getServletContext().getRequestDispatcher(DISPLAY_EXCEPTION);
                rd.forward(request, response);                
            }
        }
    }
}
