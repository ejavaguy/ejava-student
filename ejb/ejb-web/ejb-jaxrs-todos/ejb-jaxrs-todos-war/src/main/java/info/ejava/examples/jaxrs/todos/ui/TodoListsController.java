package info.ejava.examples.jaxrs.todos.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.ejava.examples.jaxrs.todos.bo.TodoItem;
import info.ejava.examples.jaxrs.todos.dto.TodoItemDTO;
import info.ejava.examples.jaxrs.todos.dto.TodoListDTO;
import info.ejava.examples.jaxrs.todos.dto.TodoListListDTO;
import info.ejava.examples.jaxrs.todos.ejb.ClientErrorException;
import info.ejava.examples.jaxrs.todos.ejb.InvalidRequestException;
import info.ejava.examples.jaxrs.todos.ejb.TodosMgmtRemote;

@WebServlet(urlPatterns= {"/ui/todo_lists"})
public class TodoListsController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(TodoListsController.class);
    
    @EJB
    private TodosMgmtRemote todosMgmt;
    private Map<String, Action> actions = new HashMap<>();

    private int getInt(String value, int defaultValue) {
        try {
            return value==null ? 0 : Integer.parseInt(value);
        } catch (Exception ex) {
            return 0;
        }
    }
    
    @Override
    public void init() throws ServletException {
        actions.put("populate", new PopulateAction());
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int offset = getInt(req.getParameter("offset"), 0);
        int limit = getInt(req.getParameter("limit"), 0);

        try {
            TodoListListDTO todoLists = todosMgmt.getTodoLists(offset, limit);
            req.setAttribute("offset", offset);
            req.setAttribute("limit", limit);            
            req.setAttribute("todoLists", todoLists.getTodoLists()!=null?
                    todoLists.getTodoLists() : Collections.emptyList());
            RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    "/WEB-INF/content/DisplayTodoLists.jsp");
            rd.forward(req, resp);            
        } catch (Exception ex) {
            logger.error("error getting todoLists:" + ex);
            req.setAttribute("exception", ex);
            RequestDispatcher rd = getServletContext().getRequestDispatcher(
                "DisplayException.jsp");
            rd.forward(req, resp);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String actionName = req.getParameter("action");
            Action action = actionName!=null ? actions.get(actionName) : null;
            if (action!=null) {
                action.execute(req, resp);
            } else {
                resp.setStatus(404);
                ServletOutputStream out = resp.getOutputStream();
                out.println(String.format("action[%s] not found", actionName));
            }            
        } catch (Exception ex) {
            logger.error("error executing action:" + ex);
            req.setAttribute("exception", ex);
            RequestDispatcher rd = getServletContext().getRequestDispatcher(
                "DisplayException.jsp");
            rd.forward(req, resp);
        }
    }

    private abstract class Action {
        public abstract void execute(HttpServletRequest req, HttpServletResponse resp) 
                throws ClientErrorException, ServletException, IOException;
    }
    
    private class PopulateAction extends Action {

        @Override
        public void execute(HttpServletRequest req, HttpServletResponse resp) 
                    throws InvalidRequestException, ServletException, IOException {
            todosMgmt.deleteAll();
            Random r = new Random();
            for (int i=0; i<20; i++) {
                TodoListDTO todoList = new TodoListDTO("List" + i);
                int items = r.nextInt(10);
                todoList.setTodoItems(new ArrayList<>(items));
                for (int j=0; j<items; j++) {
                    TodoItemDTO item = new TodoItemDTO("Item" + (char)('A'+j));
                    item.setPriority(r.nextInt(10));
                    todoList.getTodoItems().add(item);
                }
                todosMgmt.createTodoList(todoList);
            }
            TodoListListDTO todoLists = todosMgmt.getTodoLists(0, 0);
            req.setAttribute("todoLists", todoLists.getTodoLists()!=null?
                    todoLists.getTodoLists() : Collections.emptyList());
            RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    "/WEB-INF/content/DisplayTodoLists.jsp");
            rd.forward(req, resp);            
        }
    }
}
