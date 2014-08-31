<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
<html>
    <title>Find Students</title>
<body>
    <h2>Find Students</h2>
    
    <form method="get" 
        action="<%=request.getContextPath()%>/admin/jpa/registrarAdmin">
        First Name: <input type="text" name="firstName" value="%" size="25"/><p/>            
        Last Name : <input type="text" name="lastName" value="%" size="25"/><p/>                            
        Page Size : <input type="text" name="count" value="20" size="5"/><p/>                            
        <input type="hidden" name="index" value="0"/><p/>                            
        <input type="submit" name="command" value="Find Students"/>
    </form>            
</body>
</html>
