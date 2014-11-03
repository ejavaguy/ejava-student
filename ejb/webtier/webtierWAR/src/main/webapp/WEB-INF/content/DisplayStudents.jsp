<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">

<jsp:directive.page errorPage="/WEB-INF/content/ErrorPage.jsp" />
<jsp:directive.page import="java.util.*"/>
<jsp:directive.page import="org.apache.commons.logging.*"/>
<jsp:directive.page import="ejava.examples.webtier.bo.*"/>
<html>
    <title>Student Display</title>
<body>
<h2>Student Display</h2>
    <jsp:scriptlet>
        List students = (List)request.getAttribute("students");
        int index = ((Integer)request.getAttribute("index")).intValue();        
        int count = ((Integer)request.getAttribute("count")).intValue();
        int nextIndex = ((Integer)request.getAttribute("nextIndex")).intValue();
        String firstName = (String)request.getAttribute("firstName");
        String lastName = (String)request.getAttribute("lastName");
        String ctxRoot = request.getContextPath();
    </jsp:scriptlet>

    <form method="get" 
        action="<%=request.getContextPath()%>/admin/jpa/registrarAdmin">
        <ul>
            <jsp:scriptlet>
                for(Object o: students) {
                    Student s=(Student)o;            
            </jsp:scriptlet>
            <li><a href="<%=ctxRoot%>/admin/jpa/registrarAdmin?command=Get%20Student&id=<%=s.getId()%>">
                id=<%=s.getId()%>, <%=s.getFirstName()%>, <%=s.getLastName()%></a></li>
            <jsp:scriptlet>
                }
            </jsp:scriptlet>
        </ul><p/> 
        Index: <%=index%> Count: <%=count%>      
        Index: <input type="hidden" name="index" value="<%=nextIndex%>"/>
        Count: <input type="hidden" name="count" value="<%=count%>"/><p/>
        First Name: <input type="text" name="firstName" value="<%=firstName%>"/>
        Last Name: <input type="text" name="lastName" value="<%=lastName%>"/><p/>
        <input type="submit" name="command" value="Find Students"/>
    </form>

    <p/><a href="<%=request.getContextPath()%>/index.jsp">Go to Main Page</a>    
</body>
</html>
