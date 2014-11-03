<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
            
<jsp:directive.page errorPage="/WEB-INF/content/ErrorPage.jsp"/>
<jsp:directive.page import="java.util.*"/>
<jsp:directive.page import="ejava.examples.asyncmarket.bo.*"/>
<html>
    <title>Create Order</title>
    <body>
        <h2>Create Order</h2>
        <jsp:scriptlet>        
            AuctionItem item = (AuctionItem)request.getAttribute("item");
            String action=request.getContextPath() + "/model/buyer/handler";            
        </jsp:scriptlet>
                
        <p/>Id:<%= item.getId() %>
        <p/>Version:<%= item.getVersion() %>
        <p/>Mid Bid:<%= item.getMinBid() %>
        <p/>StartDate:<%= item.getStartDate() %>
        <p/>EndDate:<%= item.getEndDate() %>
        <p/>closed:<%= item.isClosed() %>
        <p/>bids:<%= item.getBids().size() %>
        <ul>
        <jsp:scriptlet>
        for(Object o : item.getBids()) {
        		Bid bid = (Bid)o;
	        </jsp:scriptlet>
	        <li><%= bid %></li>
	        <jsp:scriptlet>
	        }
        </jsp:scriptlet>
        </li>
        
        <form method="GET" action="<%=action%>">
            <input type="hidden" name="itemId" value="<%= item.getId() %>"/>
            <p/>Max Amount:<input type="text" name="maxAmount" value="20.00"/>
            <p/>Bidder:<input type="text" name="userId" value="bbuyer"/>
            <p/><input type="submit" name="command" value="Place Order"/>
        </form>
                
        <p/><a href="<%=request.getContextPath()%>/index.jsp">Go to Main Page</a>        
    </body>
</html>
            
