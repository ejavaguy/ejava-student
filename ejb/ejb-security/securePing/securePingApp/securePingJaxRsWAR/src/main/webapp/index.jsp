<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
<html>
<jsp:scriptlet>
    String principal = (request.getUserPrincipal() != null) ?
        request.getUserPrincipal().getName() : "'null'";
</jsp:scriptlet>        

<body>
    <h2>Hello SecurePing JaxRS World! [<%=principal%>]</h2>
</body>
</html>
            