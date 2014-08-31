<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
<html>
    <title>Generate Students</title>
<body>
    <h2>Generate Students</h2>
    
    <form method="post" 
          action="<%=request.getContextPath()%>/admin/jpa/registrarAdmin">
        Count: <input type="text" name="count" size="25" value="100"/><p/>            
        <input type="submit" name="command" value="Generate Students"/>
    </form>
</body>
</html>
