<%-- ErrorPage.jsp
     This page is registered to handle errors in JSP files.
     --%>
<%@ page isErrorPage="true" %>
<html>
<title>General Exception Page</title>
<body>
   <h1>General Exception Page</h1>
   <p>An error was reported by the application. More detailed information
   may follow.</p>.

   <p><% 
      java.io.PrintWriter writer = new java.io.PrintWriter(out);
      exception.printStackTrace(writer); 
   %></p>

   <p/><a href="<%=request.getContextPath()%>/index.jsp">Go to Main Page</a>
</body>
</html>

