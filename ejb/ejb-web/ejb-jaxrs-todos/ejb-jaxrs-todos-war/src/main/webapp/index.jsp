<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>ejavaTodos</title>
	</head>
<body>
    <h2>EJava Todos</h2>
    <ul>
        <li><a href="ui/todo_lists">Display Todo Lists</a>
            <ul>
                <li><form action="${pageContext.request.contextPath}/ui/todo_lists" method="POST">
                        <button name="action" value="populate"></button>
                    </form>
                </li>
            </ul>
        </li>
    </ul>
    
</body>
</html>