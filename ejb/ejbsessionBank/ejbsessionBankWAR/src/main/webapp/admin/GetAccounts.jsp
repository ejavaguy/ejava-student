<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
<html>
    <title>Get Accounts</title>
<body>
    <h2>Get Accounts</h2>    
    
    <form method="GET" 
        action="<%=request.getContextPath()%>/model/admin/handler">
        Start: <input type="text" name="index" value="0" size="25"/><p/>   
        Count: <input type="text" name="count" value="10" size="25"/><p/>   
        <input type="submit" name="command" value="Get Accounts"/>
    </form>
    
</body>
</html>
