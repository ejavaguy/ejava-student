package ejava.examples.webtier.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.webtier.dao.DAOFactory;
import ejava.examples.webtier.dao.DAOTypeFactory;
import ejava.util.jndi.JNDIUtil;

@SuppressWarnings("serial")
public class JPADAOInit extends HttpServlet {
    Log log = LogFactory.getLog(JPADAOInit.class);
    
    @PersistenceContext(unitName="webtier")
    private EntityManager em;
    
    public void init() {
        try {            
            log.debug("initializing JPA DAOs");
            DAOTypeFactory daoType = DAOFactory.getDAOTypeFactory();
            log.debug("servlet got typeFactory:" + daoType);
        }
        catch (Throwable th) {
            log.fatal("error initializing JPA",th);
            super.getServletContext().log("error initializing JPA", th);
        }
    }

    protected void doGet(HttpServletRequest request, 
                         HttpServletResponse response) 
        throws ServletException, IOException {
        
        StringBuilder text = new StringBuilder();
        try {
            InitialContext jndi = new InitialContext();
            log.debug(new JNDIUtil().dump(jndi,""));
            log.debug(new JNDIUtil().dump(jndi,"java:comp/env"));
        }
        catch (Exception ex) {
            text.append(ex.toString());            
        }
        
        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();
        pw.print("<html>");
        pw.print("<body>");
        pw.println("em=" + em);
        pw.println(text);
        pw.print("</body>");
        pw.print("</html>");
    }

    protected void doPost(HttpServletRequest request, 
                          HttpServletResponse response) 
         throws ServletException, IOException {
        doGet(request, response);
    }
    
    
}
