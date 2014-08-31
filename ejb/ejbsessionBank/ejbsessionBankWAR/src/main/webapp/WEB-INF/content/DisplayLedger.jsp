<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
            
<jsp:directive.page errorPage="/WEB-INF/content/ErrorPage.jsp"/>
<jsp:directive.page import="ejava.examples.ejbsessionbank.bo.*"/>
<html>
    <title>Ledger Display</title>
    <body>
        <h2>Ledger Display</h2>
        
        <jsp:scriptlet>Ledger ledger = (Ledger)request.getAttribute("ledger");</jsp:scriptlet>
        
        Number of Accounts: <%= ledger.getNumberOfAccounts() %><p/>
        Average Balance: <%= ledger.getAverageAssets() %><p/>
        Total Assets: <%= ledger.getTotalAssets() %><p/>
        
        <form method="GET" 
            action="<%=request.getContextPath()%>/model/admin/handler">
            <input type="submit" name="command" value="Steal All Accounts"/>
        </form>
        
        
        <p/><a href="<%=request.getContextPath()%>/index.jsp">Go to Main Page</a>        
    </body>
</html>
            
