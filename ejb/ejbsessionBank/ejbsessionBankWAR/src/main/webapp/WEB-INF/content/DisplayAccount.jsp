<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
            
<jsp:directive.page errorPage="/WEB-INF/content/ErrorPage.jsp"/>
<jsp:directive.page import="ejava.examples.ejbsessionbank.bo.*"/>
<html>
    <title>Account Display</title>
    <body>
        <h2>Account Display</h2>
        
        <jsp:scriptlet>
            Account account = (Account)request.getAttribute("account");
            String acctNum = account.getAccountNumber();
        </jsp:scriptlet>
        
        Id: <%= account.getId() %><p/>
        Account Number: <%= acctNum %><p/>
        Balance: <%= account.getBalance() %><p/>
        
        <form method="GET" 
            action="<%=request.getContextPath()%>/model/admin/handler">
            Amount $: <input type="text" name="amount" size="25"/><p/>   
            <input type="hidden" name="accountNumber" value="<%= acctNum %>"/>
            <input type="submit" name="command" value="Deposit"/>
            <input type="submit" name="command" value="Withdraw"/>
            <input type="submit" name="command" value="Close Account"/>
        </form>
        
        <p/><a href="<%=request.getContextPath()%>/index.jsp">Go to Main Page</a>        
    </body>
</html>
            
