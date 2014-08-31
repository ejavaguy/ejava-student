<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
            
<jsp:directive.page errorPage="/WEB-INF/content/ErrorPage.jsp"/>
<jsp:scriptlet>
    String action=request.getContextPath() + "/model/buyer/handler";
</jsp:scriptlet>        
<html>
    <title>Buyer Main Menu</title>
    <body>
        <h2>Buyer Main Menu</h2>
    
    <ul>
        <li> Create Account
        <form method="GET" action="<%=action%>">
            <p/>Name:<input type="text" name="name" value="Betty Buyer"/>
            <p/>userId:<input type="text" name="userId" value="bbuyer"/>
            <p/><input type="submit" name="command" value="Create Account"/>
        </form>
        </li>
        <li> Get Available Auction Items
        <form method="GET" action="<%=action%>">
            <p/><input type="submit" name="command" value="Get Available Items"/>
        </form>
        </li>
    </ul>
        
        <p/><a href="<%=request.getContextPath()%>/index.jsp">Go to Main Page</a>        
    </body>
</html>
            
