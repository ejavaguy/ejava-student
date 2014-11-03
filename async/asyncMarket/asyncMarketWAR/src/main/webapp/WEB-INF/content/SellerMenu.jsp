<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
            
<jsp:directive.page errorPage="/WEB-INF/content/ErrorPage.jsp"/>
<jsp:scriptlet>
    String action=request.getContextPath() + "/model/seller/handler";
</jsp:scriptlet>        
<html>
    <title>Seller Main Menu</title>
    <body>
        <h2>Seller Main Menu</h2>
    
    <ul>
        <li> Create Account
        <form method="GET" action="<%=action%>">
            <p/>Name:<input type="text" name="name" value="Alan Seller"/>
            <p/>userId:<input type="text" name="userId" value="aseller"/>
            <p/><input type="submit" name="command" value="Create Account"/>
        </form>
        </li>
        <li> Sell Product
        <form method="GET" action="<%=action%>">
            <p/>Name:<input type="text" name="name" value="chocolate chip cookies"/>
            <p/>Duration (secs):<input type="text" name="delay" value="60"/>
            <p/>Min Bid:<input type="text" name="midBid" value="5.00"/>
            <p/>User Id:<input type="text" name="userId" value="aseller"/>        
            <p/><input type="submit" name="command" value="Sell Product"/>
        </form>
        </li>
        <li> Get Auction Items
        <form method="GET" action="<%=action%>">
            <p/>User Id:<input type="text" name="userId" value="aseller"/>        
            <p/><input type="submit" name="command" value="Get Items"/>
        </form>
        </li>
    </ul>
        
        <p/><a href="<%=request.getContextPath()%>/index.jsp">Go to Main Page</a>        
    </body>
</html>
            
