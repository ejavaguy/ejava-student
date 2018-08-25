<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
            
<html>
<head>
   <title>General Exception Page</title>
</head>
<body>
   <center><h1>General Exception Page</h1></center>
   <p>An exception was reported by the application. More detailed information
   may follow.</p>.

   <p>
   <jsp:scriptlet>
      Exception ex = (Exception)request.getAttribute("exception");
      if (ex != null) { 
          java.io.PrintWriter writer = new java.io.PrintWriter(out);
          </jsp:scriptlet>
          </p>
          <p>Message: <%=ex.getMessage()%></p>
          <p>Details:</p>
          <p>
          <jsp:scriptlet>
          ex.printStackTrace(writer);
      } 
   </jsp:scriptlet>      
   </p>   

   <p/><a href="<%=request.getContextPath()%>/index.jsp">Go to Main Page</a>
</body>
</html>

