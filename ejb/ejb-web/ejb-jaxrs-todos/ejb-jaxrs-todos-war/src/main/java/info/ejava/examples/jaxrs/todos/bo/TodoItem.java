package info.ejava.examples.jaxrs.todos.bo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name="ETODOS_TODO_ITEM")
@NamedQuery(name="TodoItem.getTodoItem",
        query="select ti from TodoItem ti where ti.todoList.name=:listName and ti.name=:itemName"
)
public class TodoItem {
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private int id;
    
    @Column(name="NAME", length=32, nullable=false)
    private String name;
    
    @Column(name="ITEM_PRIORITY", nullable=false)
    private int priority;
    
    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="TODO_LIST_ID", nullable=false, updatable=false)
    private TodoList todoList;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public void setTodoList(TodoList todoList) {
        this.todoList = todoList;
    }
    public TodoList getTodoList() {
        return todoList;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TodoItem [id=").append(id)
               .append(", name=").append(name)
               .append(", priority=").append(priority)               
               .append("]");
        return builder.toString();
    }
}
