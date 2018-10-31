package info.ejava.examples.jaxrs.todos.dto;

import java.io.Serializable;

public class TodoItemDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private Integer priority;
    
    public TodoItemDTO() {}
    public TodoItemDTO(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Integer getPriority() {
        return priority;
    }
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Todo[name=").append(name)
                .append(", priority=").append(priority)
               .append("]");
        return builder.toString();
    }
}
