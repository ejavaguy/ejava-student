package info.ejava.examples.jaxrs.todos.client;

import javax.ws.rs.core.Response;

import info.ejava.examples.jaxrs.todos.dto.TodoItemDTO;
import info.ejava.examples.jaxrs.todos.dto.TodoListDTO;

public interface TodosJaxRsClient {
    static String APPLICATION_PATH = "api";
    static String TODO_LISTS_PATH = "todo_lists";
    static String TODO_LIST_PATH = "todo_lists/{listName}";
    static String TODO_ITEMS_PATH = "todo_items";
    static String TODO_ITEM_PATH = "todo_items/{itemName}";
    static final String OFFSET = "offset";
    static final String LIMIT = "limit";
    static final String NAME_PARAM = "name";
    
    Response createTodoList(TodoListDTO todoList);
    Response getTodoLists(Integer offset, Integer limit);
    Response getTodoList(String listName);
    Response deleteTodoList(String listName);
    Response renameTodoList(String oldName, String newName);
    
    Response addTodoItem(String listName, TodoItemDTO item);    
    Response updateTodoItem(String listName, TodoItemDTO item);
    Response deleteTodoItem(String listName, String itemName);
    
    Response deleteAll();
}
