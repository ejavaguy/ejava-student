package info.ejava.examples.jaxrs.todos.ejb;

import java.util.List;
import java.util.stream.Collectors;

import info.ejava.examples.jaxrs.todos.bo.TodoItem;
import info.ejava.examples.jaxrs.todos.bo.TodoList;
import info.ejava.examples.jaxrs.todos.dto.TodoItemDTO;
import info.ejava.examples.jaxrs.todos.dto.TodoListDTO;
import info.ejava.examples.jaxrs.todos.dto.TodoListListDTO;

public class DtoMapper {
    public TodoList map(TodoListDTO dto) {
        TodoList bo = new TodoList();
        bo.setName(dto.getName());
        
        if (dto.getTodoItems()!=null) {
            for (TodoItemDTO itemDTO: dto.getTodoItems()) {
                TodoItem itemBO = map(itemDTO);
                itemBO.setTodoList(bo);
                bo.getTodoItems().add(itemBO);
            }
        }
        return bo;
    }
    
    public TodoListDTO map(TodoList bo) {
        if (bo==null) { return null; }
        TodoListDTO dto = new TodoListDTO();
        dto.setName(bo.getName());
        List<TodoItemDTO> items = bo.getTodoItems().stream()
                                                   .map(item->map(item))
                                                   .collect(Collectors.toList());
        dto.setTodoItems(items);
        return dto;
    }
    
//    public TodoItem map(TodoItemDTO dto) {
//        TodoItem bo = new TodoItem();
//        bo.setName(dto.getName());
//        bo.setPriority(dto.getPriority());
//        bo.set
//    }
    
    public TodoItemDTO map(TodoItem bo) {
        TodoItemDTO dto = new TodoItemDTO();
        dto.setName(bo.getName());
        dto.setPriority(bo.getPriority());
        return dto;
    }

    public TodoListListDTO map(List<TodoList> bo) {
        if (bo==null) { return null; }
        List<TodoListDTO> todoLists = bo.stream()
                                        .map(list->map(list))
                                        .collect(Collectors.toList());
        TodoListListDTO dto = new TodoListListDTO();
        dto.setTodoLists(todoLists);
        return dto;
    }

    public TodoItem map(TodoItemDTO dto) {
        if (dto==null) { return null; }
        TodoItem bo = new TodoItem();
        bo.setName(dto.getName());
        if (dto.getPriority()!=null) {
            bo.setPriority(dto.getPriority());
        }
        return bo;
    }
}
