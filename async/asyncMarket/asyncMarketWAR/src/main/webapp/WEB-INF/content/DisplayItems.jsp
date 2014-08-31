<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
            
<jsp:directive.page errorPage="/WEB-INF/content/ErrorPage.jsp"/>
<jsp:directive.page import="java.util.*"/>
<jsp:directive.page import="ejava.examples.asyncmarket.bo.*"/>
<html>
    <title>Auction Items Display</title>
    <body>
        <h2>Auction Items Display</h2>
        
        <jsp:scriptlet>
            List items = (List)request.getAttribute("items");
            String handler = request.getContextPath() + "/model/buyer/handler";
        </jsp:scriptlet>
        
        <ul>
            <jsp:scriptlet>
                for(Object o: items) {
                    AuctionItem item = (AuctionItem)o;
		            </jsp:scriptlet>
		            <li><%= item %>
		            <a href="<%=handler%>?command=Bid&itemId=<%=item.getId()%>">bid</a>
		            </li>
		            <jsp:scriptlet>
                }
            </jsp:scriptlet>            
        </ul>
        <p/><a href="<%=request.getContextPath()%>/index.jsp">Go to Main Page</a>        
    </body>
</html>
            
