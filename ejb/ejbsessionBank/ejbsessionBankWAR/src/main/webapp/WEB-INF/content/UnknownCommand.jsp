<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
<jsp:directive.page errorPage="/WEB-INF/content/ErrorPage.jsp"/>
<html>
   <head>
      <title>Unknown Command</title>
   </head>
<body>
   <center><h1>Command Error</h1></center>
   A request was made, but the command was not recognized<p/>.
   command=<%=request.getParameter("command")%>
   <p/><a href="<%=request.getContextPath()%>/index.jsp">Go to Main Page</a>
</body>
</html>

