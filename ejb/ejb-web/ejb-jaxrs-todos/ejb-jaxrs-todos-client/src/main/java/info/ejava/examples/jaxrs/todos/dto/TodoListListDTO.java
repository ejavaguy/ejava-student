package info.ejava.examples.jaxrs.todos.dto;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class TodoListListDTO implements Serializable {
   private static final long serialVersionUID = 1L;
   private List<TodoListDTO> todoLists;
    
    public TodoListListDTO() {}
    public TodoListListDTO(List<TodoListDTO> todoLists) {
        this.todoLists = todoLists;
    }
    
    public List<TodoListDTO> getTodoLists() {
        return todoLists;
    }
    public void setTodoLists(List<TodoListDTO> todoLists) {
        this.todoLists = todoLists;
    }    
    public void withTodoList(TodoListDTO todoList) {
        if (todoLists==null && todoList!=null) {
            todoLists = new LinkedList<>();            
        }
        if (todoList!=null) {
            todoLists.add(todoList);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TodoList[todos=");
        if (todoLists!=null) {
            boolean first=true;
            for (TodoListDTO todoList: todoLists) {
                if (!first) { builder.append(",").append(System.lineSeparator()); }
                builder.append(todoList);
                first=false;
            }
        }
        builder.append("]");
        return builder.toString();
    }
    
    public int getCount() {
        return todoLists==null ? 0 : todoLists.size();
    }
    public void setCount(int count) { //nothing to set        
    }
}
