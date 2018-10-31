package info.ejava.examples.jaxrs.todos.bo;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table(name="ETODO_TODO_LIST")
@NamedQuery(name="TodoList.getTodoLists",
            query="select tl from TodoList tl order by tl.name asc"
)
@NamedQuery(name="TodoList.getListByName",
            query="select tl from TodoList tl where tl.name=:name"
)
@NamedQuery(name="TodoList.deleteList",
            query="delete from TodoList tl where tl.name=:name"            
)
public class TodoList {
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private int id;
    
    @Column(name="NAME", length=32, nullable=false, unique=true)
    private String name;
    
    @OneToMany(mappedBy="todoList", 
            fetch=FetchType.EAGER, 
            cascade= {CascadeType.PERSIST,
                      CascadeType.REFRESH,
                      CascadeType.DETACH,
                      CascadeType.REMOVE}, 
            orphanRemoval=true)
    @OrderBy("priority ASC")
    private List<TodoItem> todoItems;

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

    public List<TodoItem> getTodoItems() {
        if (todoItems==null) {
            todoItems=new LinkedList<>();
        }
        return todoItems;
    }

    public void setTodoItems(List<TodoItem> todoItems) {
        this.todoItems = todoItems;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TodoList [id=").append(id)
               .append(", name=").append(name)
               .append(", todoItems=");
        boolean first=true;
        for (TodoItem item: getTodoItems()) {
            if (!first) { builder.append(",").append(System.lineSeparator()); }
            builder.append(item);
            first=false;
        }
        builder.append("]");
        return builder.toString();
    }
    
    
}
