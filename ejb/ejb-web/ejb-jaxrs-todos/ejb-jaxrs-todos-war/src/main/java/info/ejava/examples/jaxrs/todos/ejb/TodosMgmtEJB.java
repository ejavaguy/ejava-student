package info.ejava.examples.jaxrs.todos.ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import info.ejava.examples.jaxrs.todos.bo.TodoItem;
import info.ejava.examples.jaxrs.todos.bo.TodoList;
import info.ejava.examples.jaxrs.todos.dto.TodoItemDTO;
import info.ejava.examples.jaxrs.todos.dto.TodoListDTO;
import info.ejava.examples.jaxrs.todos.dto.TodoListListDTO;

@Stateless
public class TodosMgmtEJB implements TodosMgmtRemote {
    @Inject
    DtoMapper dtoMapper;
    
    @Inject
    EntityManager em;

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public TodoListListDTO getTodoLists(int offset, int limit) {        
        List<TodoList> result = getTodoListsLocal(offset, limit);
        return dtoMapper.map(result);
    }
    
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<TodoList> getTodoListsLocal(int offset, int limit) {
        TypedQuery<TodoList> query=em.createNamedQuery("TodoList.getTodoLists", TodoList.class);
        if (offset>0) {
            query.setFirstResult(offset);
        }
        if (limit>0) {
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TodoListDTO createTodoList(TodoListDTO todoList) {
        try {
            TodoList result = createTodoList(dtoMapper.map(todoList));
            return dtoMapper.map(result);
        } catch (RuntimeException ex) {
            throw new InternalErrorException("Error creating todoList: %s", ex.toString());            
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TodoList createTodoList(TodoList todoList) {
        em.persist(todoList);
        return todoList;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public TodoListDTO getTodoList(String listName) throws ResourceNotFoundException {
        try {
            TodoList result = getTodoListLocal(listName);
            if (result==null) {
                throw new ResourceNotFoundException("listName[%s] not found", listName);
            }
            return dtoMapper.map(result);
        } catch (RuntimeException ex) {            
            throw new InternalErrorException("Error getting todoList: %s", ex.toString());
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public TodoList getTodoListLocal(String listName) {
        List<TodoList> results = em.createNamedQuery("TodoList.getListByName", 
                TodoList.class)
                 .setParameter("name", listName)
                .getResultList();        
        return results.isEmpty() ? null : results.get(0);
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TodoListDTO renameTodoList(String oldName, String newName) throws ResourceNotFoundException {
        try {
            TodoList todoList = getTodoListLocal(oldName);
            if (todoList==null) {
                throw new ResourceNotFoundException("todoList[%s] not found", oldName);
            }
            todoList.setName(newName);
            return dtoMapper.map(todoList);
        } catch (RuntimeException ex) {
            throw new InternalErrorException("Error renaming todoList: %s", ex.toString());            
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteTodoList(String listName) throws ResourceNotFoundException {
        try {
            if (deleteTodoListLocal(listName)==0) {
                throw new ResourceNotFoundException("todoList[%s] not found", listName);
            }
        } catch (RuntimeException ex) {
            throw new InternalErrorException("Error deleting todoList: %s", ex.toString());            
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int deleteTodoListLocal(String listName) {
        TodoList todoList = getTodoListLocal(listName);
        if (todoList==null) {
            return 0;
        } else {
            em.remove(todoList); //use casecades
            return 1;
        }            
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void addTodoListItem(String listName, TodoItemDTO item) 
            throws ResourceNotFoundException, InvalidRequestException {
        try {
            TodoList todoList = getTodoListLocal(listName);
            if (todoList==null) {
                throw new ResourceNotFoundException("todoList[%s] not found", listName);
            }
            TodoItem itemBO = dtoMapper.map(item);
            if (itemBO==null) {
                throw new InvalidRequestException("required item not supplied");
            }
            
            addTodoListItem(todoList, itemBO);
        } catch (RuntimeException ex) {
            throw new InternalErrorException("Error adding listItem to todoList[%s]: %s", listName, ex.toString());            
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void addTodoListItem(TodoList todoList, TodoItem item) {
        item.setTodoList(todoList);
        todoList.getTodoItems().add(item);
        em.persist(item);
        em.flush();
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public TodoItem getTodoListItem(String listName, String itemName) {
        List<TodoItem> results = em.createNamedQuery("TodoItem.getTodoItem", TodoItem.class)
                .setParameter("listName", listName)
                .setParameter("itemName", itemName)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TodoItemDTO updateTodoListItem(String listName, String itemName, TodoItemDTO item) 
            throws ResourceNotFoundException {
        try {
            TodoItem dbCopy = getTodoListItem(listName, itemName);
            if (dbCopy==null) {
                throw new ResourceNotFoundException("todoList[%s], todoItem[%s] not found", listName, itemName);
            }
                //assign the PK from the DB copy retrieved by distinct list+item name
            TodoItem toUpdate = dtoMapper.map(item);
            toUpdate.setId(dbCopy.getId());
            
            return dtoMapper.map(updateTodoListItem(toUpdate));
        } catch (RuntimeException ex) {
            throw new InternalErrorException("Error updating todoList: %s", ex.toString());            
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TodoItem updateTodoListItem(TodoItem item) {
        return em.merge(item);
    }


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteTodoListItem(String listName, String itemName) throws ResourceNotFoundException {
        try {
            TodoItem item = getTodoListItem(listName, itemName);
            if (item==null) {
                throw new ResourceNotFoundException("todoList[%s], todoItem[%s] not found", listName, itemName);
            }
            deleteTodoListItem(item);
        } catch (RuntimeException ex) {
            throw new InternalErrorException("Error deleting todoList: %s", ex.toString());            
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteTodoListItem(TodoItem item) {
        em.createNamedQuery("TodoItem.deleteTodoItem")
            .setParameter("id", item.getId())
            .executeUpdate();
    }

    
    @Override
    public void deleteAll() {
        for (String entityName : new String[] {"TodoItem","TodoList"}) {
            em.createQuery(String.format("delete from %s o", entityName)).executeUpdate();
        }
    }
}
