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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.util.jndi.JNDIUtil;

@SuppressWarnings("serial")
public class JPADAOInit extends HttpServlet {
    private Logger logger = LoggerFactory.getLogger(JPADAOInit.class);
    
    @PersistenceContext(unitName="webtier")
    private EntityManager em;
    
    protected void doGet(HttpServletRequest request, 
                         HttpServletResponse response) 
        throws ServletException, IOException {
        
        StringBuilder text = new StringBuilder();
        try {
            InitialContext jndi = new InitialContext();
            logger.debug(new JNDIUtil().dump(jndi,""));
            logger.debug(new JNDIUtil().dump(jndi,"java:comp/env"));
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
}
