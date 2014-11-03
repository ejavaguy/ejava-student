<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
            
<jsp:directive.page errorPage="/WEB-INF/content/ErrorPage.jsp"/>
<jsp:directive.page import="java.util.*"/>
<jsp:directive.page import="ejava.examples.asyncmarket.bo.*"/>
<html>
    <title>Order Display</title>
    <body>
        <h2>Item Display</h2>
        <jsp:scriptlet>
            Order order = (Order)request.getAttribute("order");
            String handler = request.getContextPath() + "/model/buyer/handler";  
        </jsp:scriptlet>
                
        <p/><%= order %>
        
        <p/><a href="<%= handler%>?command=Get+Order&orderId=<%=order.getId()%>">
        Refresh</a>
                
        <p/><a href="<%=request.getContextPath()%>/index.jsp">Go to Main Page</a>        
    </body>
</html>
            
