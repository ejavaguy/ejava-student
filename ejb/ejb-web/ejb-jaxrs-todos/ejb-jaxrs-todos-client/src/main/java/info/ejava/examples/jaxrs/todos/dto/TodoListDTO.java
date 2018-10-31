package info.ejava.examples.jaxrs.todos.dto;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class TodoListDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private List<TodoItemDTO> todoItems;
    
    public TodoListDTO() {}
    
    public TodoListDTO(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public List<TodoItemDTO> getTodoItems() {
        return todoItems;
    }
    public void setTodoItems(List<TodoItemDTO> todoItems) {
        this.todoItems = todoItems;
    }
    public void withTodoItem(TodoItemDTO todoItem) {
        if (todoItems==null && todoItem!=null) {
            todoItems = new LinkedList<>();            
        }
        if (todoItem!=null) {
            todoItems.add(todoItem);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TodoList[name=").append(name)
               .append(", todoItems=");
        if (todoItems!=null) {
            boolean first=true;
            for (TodoItemDTO todo: todoItems) {
                if (!first) { builder.append(",").append(System.lineSeparator()); }
                builder.append(todo);
                first=false;
            }
        }
        builder.append("]");
        return builder.toString();
    }
}
