package ejava.examples.webtier.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.webtier.bl.Registrar;
import ejava.examples.webtier.bl.RegistrarException;
import ejava.examples.webtier.bl.RegistrarImpl;
import ejava.examples.webtier.bo.Student;
import ejava.examples.webtier.dao.StudentDAO;
import ejava.examples.webtier.jpa.StudentJPADAO;

@SuppressWarnings("serial")
public class RegistrarHandlerServlet extends HttpServlet {
    private Logger logger = LoggerFactory.getLogger(RegistrarHandlerServlet.class);
    private Map<String, Handler> handlers = new HashMap<String, Handler>();

    public void init() {
        String level = super.getServletConfig().getInitParameter("level");  
        logger.info("level=" + level);
        
        //create proper handlers per role
        if (level != null && "admin".equals(level)) {
            handlers.put("Create Student", new CreateStudent());
            handlers.put("Generate Students",  new GenerateStudents());
            handlers.put("Find Students", new GetStudents());
            handlers.put("Get Student", new GetStudent());
            handlers.put("Remove Student", new RemoveStudent());
        }
        else if (level != null && "user".equals(level)) {
            
        }
        else {
            
        }
    }
    
    /**
     * Create a new instance of the registrar per request so that we do not
     * have two calling threads using the same instance. JPAFilter will return
     * the same EntityManager instance to all calls within the same thread.
     * @return
     */
    protected Registrar getRegistrar() {
        EntityManager em = JPAFilter.getEntityManager();
        StudentDAO studentDao = new StudentJPADAO();
        ((StudentJPADAO)studentDao).setEntityManager(em);
        Registrar registrar = new RegistrarImpl();
        ((RegistrarImpl)registrar).setStudentDAO(studentDao);
        return registrar;
    }
    
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        
        String command = request.getParameter("command");
        if (command != null) {
            Handler handler = handlers.get(command);
            if (handler != null) {
                handler.handle(request, response);
            }
            else {
                RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    "/WEB-INF/content/UnknownCommand.jsp");
                    rd.forward(request, response);
            }
        }
        else {
            RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    "NoCommand.jsp");
                    rd.forward(request, response);
        }

        
    }

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private abstract class Handler {
        public abstract void handle(HttpServletRequest request,
                                    HttpServletResponse response)
                                    throws ServletException, IOException;
    }
    
    private class CreateStudent extends Handler {
        public void handle(HttpServletRequest request,
                HttpServletResponse response)
                throws ServletException, IOException {
            Student student = new Student();
            student.setFirstName(request.getParameter("firstName"));
            student.setLastName(request.getParameter("lastName"));
            
            try {
                logger.debug("creating new student:" + student);
                Student newStudent = getRegistrar().addStudent(student);
                request.setAttribute("student", newStudent);
                logger.debug("new student created:" + student);
                
                RequestDispatcher rd = getServletContext().getRequestDispatcher(
                        "/WEB-INF/content/DisplayStudent.jsp");
                rd.forward(request, response);
            } catch (RegistrarException ex) {
                logger.error("error creating student:" + ex);
                request.setAttribute("exception", ex);
                RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    "DisplayException.jsp");
                rd.forward(request, response);
            }
            
        }        
    }
    
    private class GenerateStudents extends Handler {
        public void handle(HttpServletRequest request,
                HttpServletResponse response)
                throws ServletException, IOException {
            String countStr = request.getParameter("count");
            int count = 100;
            try { count = Integer.parseInt(countStr); }
            catch(Exception ex) {}
            
            try {
                logger.debug("generating " + count + " students");
                List<Student> students = new ArrayList<Student>();
                for(int i=0; i<count; i++) {
                    Student student = new Student();
                    student.setFirstName("gen");
                    student.setLastName("student" + i);
                    @SuppressWarnings("unused")
                    Student newStudent = getRegistrar().addStudent(student);
                    students.add(student);
                }
                logger.debug("new students created");
                request.setAttribute("students", students);
                request.setAttribute("index", 0);
                request.setAttribute("count", count);
                request.setAttribute("nextIndex", 0);
                request.setAttribute("firstName", "%");
                request.setAttribute("lastName", "%");
                
                RequestDispatcher rd = getServletContext().getRequestDispatcher(
                        "/WEB-INF/content/DisplayStudents.jsp");
                rd.forward(request, response);
            } catch (RegistrarException ex) {
                logger.error("error generating students:" + ex);
                request.setAttribute("exception", ex);
                RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    "DisplayException.jsp");
                rd.forward(request, response);
            }
            
        }        
    }

    
    private class GetStudents extends Handler {
        public void handle(HttpServletRequest request,
                HttpServletResponse response)
                throws ServletException, IOException {
            String indexStr = request.getParameter("index");
            String countStr = request.getParameter("count");
            String firstName = (String)request.getParameter("firstName");
            firstName = (firstName == null ? "%" : firstName);
            String lastName = (String)request.getParameter("lastName");
            lastName = (lastName == null ? "%" : lastName);
            
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("firstName", firstName);
            params.put("lastName", lastName);
            
            int index = 0;
            try { index = Integer.parseInt(indexStr); }
            catch(Exception ex) {
                logger.info("error in index format (" + indexStr +
                        ") defaulting to " + index);
            }
            int count = 20;
            try { count = Integer.parseInt(countStr); }
            catch(Exception ex) {
                logger.info("error in count format (" + countStr +
                        ") defaulting to " + count);
            }            
            
            try {
                logger.debug("getting students(" + index + ", " + count + "):" +
                        params);
                List<Student> students = 
                    getRegistrar().getStudents("getStudentsByName", 
                            params, index, count);
                int nextIndex = (students.size() < count) ?
                        0 : index + count;
                request.setAttribute("students", students);
                request.setAttribute("index", index);
                request.setAttribute("count", count);
                request.setAttribute("nextIndex", nextIndex);
                request.setAttribute("firstName", firstName);
                request.setAttribute("lastName", lastName);
                logger.debug("students found:" + students.size());
                
                RequestDispatcher rd = getServletContext().getRequestDispatcher(
                        "/WEB-INF/content/DisplayStudents.jsp");
                rd.forward(request, response);
            } catch (RegistrarException ex) {
                logger.error("error getting students:" + ex);
                request.setAttribute("exception", ex);
                RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    "/WEB-INF/content/DisplayException.jsp");
                rd.forward(request, response);
            }
            
        }        
    }
    
    private class GetStudent extends Handler {
        public void handle(HttpServletRequest request,
                HttpServletResponse response)
                throws ServletException, IOException {
            String idStr = request.getParameter("id");
            long id = 0;
            try { id = Long.parseLong(idStr); }
            catch(Exception ex) {
                logger.info("error in id format:" + idStr);
                request.setAttribute("exception", ex);
                RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    "/WEB-INF/content/DisplayException.jsp");
                rd.forward(request, response);
            }
            
            
            
            try {
                logger.debug("getting student:");
                Student student = getRegistrar().getStudent(id);
                request.setAttribute("student", student);
                logger.debug("student found:" + student);
                
                RequestDispatcher rd = getServletContext().getRequestDispatcher(
                        "/WEB-INF/content/DisplayStudent.jsp");
                rd.forward(request, response);
            } catch (RegistrarException ex) {
                logger.error("error getting student:" + ex);
                request.setAttribute("exception", ex);
                RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    "/WEB-INF/content/DisplayException.jsp");
                rd.forward(request, response);
            }
            
        }        
    }

    
    private class RemoveStudent extends Handler {
        public void handle(HttpServletRequest request,
                HttpServletResponse response)
                throws ServletException, IOException {
            String idStr = request.getParameter("id");
            long id = 0;
            try { id = Long.parseLong(idStr); }
            catch(Exception ex) {
                logger.info("error in id format:" + idStr);
                request.setAttribute("exception", ex);
                RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    "/WEB-INF/content/DisplayException.jsp");
                rd.forward(request, response);
            }
            
            
            try {
                logger.debug("removing student:");
                Student student = getRegistrar().getStudent(id); 
                student = getRegistrar().dropStudent(student);
                request.setAttribute("student", student);
                request.setAttribute("removed", true);
                logger.debug("student removed:" + student);
                
                RequestDispatcher rd = getServletContext().getRequestDispatcher(
                        "/WEB-INF/content/DisplayStudent.jsp");
                rd.forward(request, response);
            } catch (RegistrarException ex) {
                logger.error("error getting student:" + ex);
                request.setAttribute("exception", ex);
                RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    "/WEB-INF/content/DisplayException.jsp");
                rd.forward(request, response);
            }
            
        }        
    }
    
}
