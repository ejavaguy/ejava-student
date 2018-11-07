<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">

<jsp:directive.page errorPage="/WEB-INF/content/ErrorPage.jsp" />
<jsp:directive.page import="java.util.*"/>
<jsp:directive.page import="info.ejava.examples.jaxrs.todos.dto.*"/>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <title>Todo List</title>
<body>
<h2>Todo List: ${requestScope.todoList.name}</h2>
    <div>
        <table>
            <tbody>
                <tr>
                    <th>Task</th>
                    <th>Priority</th>
                </tr>                
            </tbody>
        <c:forEach items="${requestScope.todoList.todoItems}" var="item">
            <tr>
                <td><c:out value="${item.name}"></c:out></td>
                <td>
                    <form action="${pageContext.request.contextPath}/ui/todo_lists/${requestScope.todoList.name}/todo_items/${item.name}" 
                          method="POST">
                        <input type="number" min="0" max="10" name="priority" value="${item.priority}">
                        <button name="action" value="setPriority">Update</button>
                        <button name="action" value="deleteItem">Delete</button>
                    </form>
                </td>
            </tr>
        </c:forEach>  
        </table>
    </div>
    
    <div>
        <a href="<%=request.getContextPath()%>">Go to Main Page</a>    
    </div>
    
</body>
</html>
