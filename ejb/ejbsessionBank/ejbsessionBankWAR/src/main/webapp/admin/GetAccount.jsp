<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
<html>
    <title>Get Account</title>
<body>
    <h2>Get Account</h2>    
    
    <form method="GET" 
        action="<%=request.getContextPath()%>/model/admin/handler">
        Account Number: <input type="text" name="accountNumber" size="25"/><p/>   
        <input type="submit" name="command" value="Get Account"/>
    </form>
    
</body>
</html>
