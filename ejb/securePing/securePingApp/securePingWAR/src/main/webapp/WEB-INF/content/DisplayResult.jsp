<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
            
<jsp:directive.page errorPage="/WEB-INF/content/ErrorPage.jsp"/>
<jsp:directive.page import="java.util.*"/>
<jsp:scriptlet>
    String principal = (request.getUserPrincipal() != null) ?
    request.getUserPrincipal().getName() : "'null'";
</jsp:scriptlet>        
<html>
    <title><%=principal%> Result Display</title>
    <body>
        <h2><%=principal%> Result Display</h2>
        
        <%= request.getAttribute("result") %>
                
        <p/><a href="<%=request.getContextPath()%>/index.jsp">Go to Main Page</a>        
    </body>
</html>
            
