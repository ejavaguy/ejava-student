<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
<html>
    <title>Create Person</title>
<body>
    <h2>Create Person</h2>    
    
    <form method="GET" 
        action="<%=request.getContextPath()%>/model/admin/handler">
        First Name: <input type="text" name="firstName" size="25"/><p/>   
        Last Name : <input type="text" name="lastName" size="25"/><p/>         
        <input type="submit" name="command" value="Create Person"/>
    </form>
</body>
</html>
            