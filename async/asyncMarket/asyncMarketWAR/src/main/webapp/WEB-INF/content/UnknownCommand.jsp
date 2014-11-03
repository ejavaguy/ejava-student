<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
<jsp:directive.page errorPage="/WEB-INF/content/ErrorPage.jsp"/>
<jsp:directive.page import="java.util.*"/>
<html>
   <header>
      <title>Unknown Command</title>
   </header>
<body>
   <center><h1>Command Error</h1></center>
   A request was made, but the command was not recognized</p>.
   command=<%=request.getParameter("command")%>
   <p/><a href="<%=request.getContextPath()%>/index.jsp">Go to Main Page</a>
   
   <jsp:scriptlet>
       Map handlers = (Map)request.getAttribute("handlers");
       if (handlers != null) {
           </jsp:scriptlet>
           <h3>Known Handlers</h3>
           <ul>
	           <jsp:scriptlet>
	           for(Object name: handlers.keySet()) {
		           </jsp:scriptlet>
		           <li><%=name%></li>
		           <jsp:scriptlet>
	           }
	           </jsp:scriptlet>
           </ul>
           <jsp:scriptlet>
       }
   </jsp:scriptlet>
</body>
</html>

