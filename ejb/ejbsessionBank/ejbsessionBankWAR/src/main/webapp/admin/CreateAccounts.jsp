<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
<html>
    <title>Create Accounts</title>
<body>
    <h2>Create Accounts</h2>    
    
    <form method="GET" 
        action="<%=request.getContextPath()%>/model/admin/handler">
        Number of Accounts: <input type="text" name="count" value="100"/><p/>   
        <input type="submit" name="command" value="Create Accounts"/>
    </form>
</body>
</html>
