<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
            
<jsp:directive.page errorPage="/WEB-INF/content/ErrorPage.jsp"/>
<jsp:scriptlet>
    String action=request.getContextPath() + "/model/admin/handler";
</jsp:scriptlet>        
<html>
    <title>Admin Main Menu</title>
    <body>
        <h2>Admin Main Menu</h2>
    
    <ul>
        <li>
        <form method="GET" action="<%=action%>">
            <input type="submit" name="command" value="Cancel Timers"/>
        </form>
        </li>
        <li>
        <form method="GET" action="<%=action%>">
            <input type="text" name="checkIntervalTimer" value="10000"/>
            <input type="submit" name="command" value="Init Timers"/>
        </form>
        </li>
        <li>
        <form method="GET" action="<%=action%>">
            <input type="text" name="userId" value=""/>
            <input type="submit" name="command" value="Remove Account"/>
        </form>
        </li>
    </ul>
        
        <p/><a href="<%=request.getContextPath()%>/index.jsp">Go to Main Page</a>        
    </body>
</html>
            
