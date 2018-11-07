package info.ejava.examples.jaxrs.todos.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.ejava.examples.jaxrs.todos.dto.TodoItemDTO;
import info.ejava.examples.jaxrs.todos.dto.TodoListDTO;
import info.ejava.examples.jaxrs.todos.dto.TodoListListDTO;
import info.ejava.examples.jaxrs.todos.ejb.ClientErrorException;
import info.ejava.examples.jaxrs.todos.ejb.TodosMgmtRemote;

@WebServlet(urlPatterns= {"/ui/todo_lists/*"})
public class TodoListController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(TodoListController.class);
    
    @EJB
    private TodosMgmtRemote todosMgmt;
    private Map<String, Action> actions = new HashMap<>();
    
    @Override
    public void init() throws ServletException {
        actions.put("deleteList", new DeleteTodoListAction());
        actions.put("setPriority", new SetPriorityAction());        
        actions.put("deleteItem", new DeleteTodoItemAction());        
    }
    
    private int getInt(String value, int defaultValue) {
        try {
            return value==null ? 0 : Integer.parseInt(value);
        } catch (Exception ex) {
            return 0;
        }
    }
    
    private Map<String, String> getPathParams(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        String[] tokens = pathInfo.split("/");
        String listName = null;
        String itemName = null;
        for (int i=0; i<tokens.length; i++) {
            if (i==1) {
                listName = tokens[i];                
            } else if ("todo_items".equals(tokens[i]) && i+1<tokens.length) {
                itemName = tokens[++i];
            }
        }
        Map<String, String> pathParams = new HashMap<>();
        if (listName!=null) {
            pathParams.put("listName", listName);
            if (itemName!=null) {
                pathParams.put("itemName", itemName);
            } 
        }
        return pathParams;
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> pathParams = getPathParams(req);
        String listName = pathParams.get("listName");

        try {
            TodoListDTO todoList = todosMgmt.getTodoList(listName);
            req.setAttribute("todoList", todoList);
            RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    "/WEB-INF/content/DisplayTodoList.jsp");
            rd.forward(req, resp);
        } catch (Exception ex) {
            logger.error("error getting todoList:" + ex);
            req.setAttribute("exception", ex);
            RequestDispatcher rd = getServletContext().getRequestDispatcher(
                "/WEB-INF/content/DisplayException.jsp");
            rd.forward(req, resp);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> pathParams = getPathParams(req);
        String listName = pathParams.get("listName");
        String itemName = pathParams.get("itemName");
        
        try {
            String actionName = req.getParameter("action");
            if (actionName==null) {
                throw new ServletException("no action provided");
            }
            
            Action action = actions.get(actionName);
            if (action==null) {
                throw new ServletException("no action found");
            }
            
            TodoListDTO todoList = todosMgmt.getTodoList(listName);
            if (todoList==null) {
                throw new ServletException("listName not found");                
            }
            
            TodoItemDTO todoItem = itemName!=null ? todoList.getListItem(itemName) : null;
            if (itemName!=null && todoItem==null) {
                throw new ServletException("itemName not found within todoList");
            }
            
            action.execute(req, resp, todoList, todoItem);
        } catch (Exception ex) {
            logger.error("error getting todoList:" + ex);
            req.setAttribute("exception", ex);
            RequestDispatcher rd = getServletContext().getRequestDispatcher(
                "/WEB-INF/content/DisplayException.jsp");
            rd.forward(req, resp);
        }
    }

    private interface Action {
        void execute(HttpServletRequest req, HttpServletResponse resp,
                TodoListDTO todoList, TodoItemDTO todoItem) 
                throws ClientErrorException, ServletException, IOException;
    }
    
    private class DeleteTodoListAction implements Action {
        @Override
        public void execute(HttpServletRequest req, HttpServletResponse resp,
                TodoListDTO todoList, TodoItemDTO todoItem) 
                        throws ClientErrorException, ServletException, IOException {
            todosMgmt.deleteTodoList(todoList.getName());
            TodoListListDTO todoLists = todosMgmt.getTodoLists(0, 0);
            
            req.setAttribute("todoLists", todoLists.getTodoLists());
            RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    "/WEB-INF/content/DisplayTodoLists.jsp");
            rd.forward(req, resp);
        }        
    }
    
    private class SetPriorityAction implements Action {
        @Override
        public void execute(HttpServletRequest req, HttpServletResponse resp,
                TodoListDTO todoList, TodoItemDTO todoItem) 
                        throws ClientErrorException, ServletException, IOException {
            int priority = getInt(req.getParameter("priority"), 10);
            todoItem.setPriority(priority);
            todosMgmt.updateTodoListItem(todoList.getName(), todoItem.getName(), todoItem);
                //get list with new sorted order
            todoList = todosMgmt.getTodoList(todoList.getName());
            
            req.setAttribute("todoList", todoList);
            RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    "/WEB-INF/content/DisplayTodoList.jsp");
            rd.forward(req, resp);
        }
    }
    
    private class DeleteTodoItemAction implements Action {
        @Override
        public void execute(HttpServletRequest req, HttpServletResponse resp,
                TodoListDTO todoList, TodoItemDTO todoItem) 
                        throws ClientErrorException, ServletException, IOException {
            todosMgmt.deleteTodoListItem(todoList.getName(), todoItem.getName());
                //get list with new sorted order
            todoList = todosMgmt.getTodoList(todoList.getName());
            
            req.setAttribute("todoList", todoList);
            RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    "/WEB-INF/content/DisplayTodoList.jsp");
            rd.forward(req, resp);
        }
    }
}
