<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
            
<jsp:directive.page errorPage="/WEB-INF/content/ErrorPage.jsp"/>
<jsp:directive.page import="java.util.*"/>
<jsp:directive.page import="ejava.examples.asyncmarket.bo.*"/>
<html>
    <title>User Display</title>
    <body>
        <h2>User Display</h2>
        
        <jsp:scriptlet>
            Person user = (Person)request.getAttribute("user");
        </jsp:scriptlet>
        
        
        <p/>UserId:<%= user.getUserId() %>
        <p/>Name:<%= user.getName() %>
        <p/>Id:<%= user.getId() %>
        <p/>Version:<%= user.getVersion() %>
        <p/>Bids:<%= user.getBids().size() %>
            <ul>
        <jsp:scriptlet>
	        for(Object o : user.getBids()) {
	            Bid bid = (Bid)user.getBids();
		        </jsp:scriptlet>
		            <li><%= o %></li>
		        <jsp:scriptlet>
	        }
	        </jsp:scriptlet>
            </ul>
        <p/>Items:<%= user.getItems().size() %>
            <ul>
        <jsp:scriptlet>
	        for(Object o : user.getItems()) {
	            AuctionItem item = (AuctionItem)o;
		        </jsp:scriptlet>
		            <li><%= item %></li>
		        <jsp:scriptlet>
	        }
	        </jsp:scriptlet>
            </ul>
        
        <p/><a href="<%=request.getContextPath()%>/index.jsp">Go to Main Page</a>        
    </body>
</html>
            
