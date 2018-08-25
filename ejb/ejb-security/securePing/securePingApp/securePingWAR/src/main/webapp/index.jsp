<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
<html>
<jsp:scriptlet>
    String principal = (request.getUserPrincipal() != null) ?
        request.getUserPrincipal().getName() : "'null'";
</jsp:scriptlet>        

<body>
    <h2>Hello SecurePing World! [<%=principal%>]</h2>
    
    Use the links below to access a main menu. Depending on which link you 
    choose, you will be prompted for a login (or not). If you login with
    the role required for that path, you will reach the main menu under
    that identity. If you choose a path to get to the main menu that does 
    not require a login, you will have no identity. Either way, you will
    then be given the chance to invoke methods on the EJB that will
    pass or fail depending on your assigned roles.
    <ul>
        <li><a href="model/admin/handler?command=menu">Admin Menu</a></li>
        <li><a href="model/user/handler?command=menu">User Menu</a></li>
        <li><a href="model/handler?command=menu">General Menu</a></li>
    </ul>
</body>
</html>
            