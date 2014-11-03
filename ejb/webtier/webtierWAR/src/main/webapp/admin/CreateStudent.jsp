<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
<html>
    <title>Create Student</title>
<body>
    <h2>Create Student</h2>    
    
    <form method="get" 
        action="<%=request.getContextPath()%>/admin/jpa/registrarAdmin">
        First Name: <input type="text" name="firstName" size="25"/><p/>            
        Last Name : <input type="text" name="lastName" size="25"/><p/>                            
        <input type="submit" name="command" value="Create Student"/>
    </form>
</body>
</html>
