<html>
<title>General Exception Page</title>
<body>
   <h1>General Exception Page</h1>
   <p>An error was reported by the application. More detailed information
   may follow.</p>.

   <p>
   <jsp:scriptlet>
      Exception ex = (Exception)request.getAttribute("exception");
      if (ex != null) { 
          java.io.PrintWriter writer = new java.io.PrintWriter(out);
          ex.printStackTrace(writer);
      } 
   </jsp:scriptlet>      
   </p>   
</body>
</html>

