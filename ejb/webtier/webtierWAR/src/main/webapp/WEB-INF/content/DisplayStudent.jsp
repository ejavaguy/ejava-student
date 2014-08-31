<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
            
<jsp:directive.page errorPage="/WEB-INF/content/ErrorPage.jsp"/>
<jsp:directive.page import="java.util.*"/>
<jsp:directive.page import="org.apache.commons.logging.*"/>
<jsp:directive.page import="ejava.examples.webtier.bo.*"/>
<html>
    <title>Display Student</title>
    <body>
        
        <jsp:scriptlet>
            Student student = (Student)request.getAttribute("student");
            Boolean removed = (Boolean)request.getAttribute("removed");                        
        </jsp:scriptlet>
        
        
        <jsp:scriptlet>
            if (removed == null) {
        </jsp:scriptlet>
                <h2>Student</h2>
        <jsp:scriptlet>
            } else {
        </jsp:scriptlet>
                <h2>Removed Student</h2>
        <jsp:scriptlet>
            } 
        </jsp:scriptlet>
        
        ID: <%=student.getId()%><p/>        
        First Name: <%=student.getFirstName()%><p/>        
        Last Name: <%=student.getLastName()%><p/>
        
        
        <jsp:scriptlet>
            if (removed == null) {
        </jsp:scriptlet>
        <form method="get" 
            action="<%=request.getContextPath()%>/admin/jpa/registrarAdmin">
            <input type="hidden" name="id" value="<%=student.getId()%>"/><p/>                            
            <input type="submit" name="command" value="Remove Student"/>
        </form>            
        <jsp:scriptlet>
            } 
        </jsp:scriptlet>
        
          
        <p/><a href="<%=request.getContextPath()%>/index.jsp">Go to Main Page</a>        
    </body>
</html>
            