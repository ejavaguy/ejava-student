package info.ejava.examples.jaxrs.todos.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class MarshallingTest {
    private Logger logger = LoggerFactory.getLogger(MarshallingTest.class);
    
    protected abstract <T> String marshal(T object) throws Exception;
    protected abstract <T> T demarshal(Class<T> type, String buffer) throws Exception;
    
    private TodoListDTO buildTodoList(String name) {
        TodoListDTO todoList = new TodoListDTO();
        todoList.setName(name);
        todoList.setTodoItems(new LinkedList<>());
        for (int i=0; i<3; i++) {
            TodoItemDTO item = new TodoItemDTO();
            item.setName("item" + i);
            item.setPriority(i);
            todoList.getTodoItems().add(item);
        }
        return todoList;
    }
    
    @Test
    public void message() throws Exception {
        MessageDTO msg = new MessageDTO("sample text");
        
        String buffer = marshal(msg);
        MessageDTO result = demarshal(MessageDTO.class, buffer);
        
        assertEquals("", msg.getText(), result.getText());
    }

    @Test
    public void todoItem() throws Exception {
        TodoItemDTO item = new TodoItemDTO();
        item.setName("item1");
        item.setPriority(13);
        
        String buffer = marshal(item);
        TodoItemDTO result = demarshal(TodoItemDTO.class, buffer);
        
        assertEquals("unexpected name", item.getName(), result.getName());
        assertEquals("unexpected priority", item.getPriority(), result.getPriority());
    }
    
    @Test
    public void todoList() throws Exception {
        TodoListDTO todoList = buildTodoList("testA");
        
        String buffer = marshal(todoList);
        TodoListDTO result = demarshal(TodoListDTO.class, buffer);
        
        assertEquals("unexpected name", todoList.getName(), result.getName());
        assertEquals("unexpected item size", todoList.getTodoItems().size(), result.getTodoItems().size());
        
        Map<String, TodoItemDTO> name2item = result.getTodoItems()
                                                   .stream()
                                                   .collect(Collectors.toMap(i->i.getName(), i->i));
        for (TodoItemDTO item: todoList.getTodoItems()) {
            TodoItemDTO r = name2item.get(item.getName());
            assertNotNull("name not found:" + item.getName(), r);
            assertEquals("unexpected priority", item.getPriority(), r.getPriority());
        }
    }
    
    @Test
    public void todoListList() throws Exception {
        TodoListListDTO todoLists = new TodoListListDTO(new ArrayList<>());
        for (int i=0; i<3; i++) {
            todoLists.getTodoLists().add(buildTodoList("list"+i));
        }
        
        String buffer = marshal(todoLists);
        TodoListListDTO result = demarshal(TodoListListDTO.class, buffer);
        
        assertEquals("unexpected list count", todoLists.getCount(), result.getCount());
        
        Map<String, TodoListDTO> name2list = result.getTodoLists()
                .stream()
                .collect(Collectors.toMap(list->list.getName(), list->list));
        for (TodoListDTO list: todoLists.getTodoLists()) {
            TodoListDTO r = name2list.get(list.getName());
            assertNotNull("name not found:" + list.getName(), r);
        }
    }
}
