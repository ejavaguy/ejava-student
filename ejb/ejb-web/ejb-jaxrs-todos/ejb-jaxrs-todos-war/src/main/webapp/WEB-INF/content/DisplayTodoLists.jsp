<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">

<jsp:directive.page errorPage="/WEB-INF/content/ErrorPage.jsp" />
<jsp:directive.page import="java.util.*"/>
<jsp:directive.page import="info.ejava.examples.jaxrs.todos.dto.*"/>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <title>Todo Lists</title>
<body>
<h2>Todo Lists</h2>
    <div>
        <table>
            <thead>
                <tr>
                    <th>Name</th>
                </tr>                
            </thead>
            <tbody>
        <c:forEach items="${requestScope.todoLists}" var="todoList">
            <tr>
                <td>
                    <a href="<c:url value="/ui/todo_lists/${todoList.name}"/>">
                        <c:out value="${todoList.name}"></c:out>
                    </a>
                </td>
                <td>
                    <form action="${pageContext.request.contextPath}/ui/todo_lists/${todoList.name}" 
                          method="POST">
                        <button name="action" value="deleteList">Delete</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
            </tbody>  
        </table>
    </div>
    
    <div>
        <a href="<%=request.getContextPath()%>">Go to Main Page</a>    
    </div>
    
</body>
</html>
