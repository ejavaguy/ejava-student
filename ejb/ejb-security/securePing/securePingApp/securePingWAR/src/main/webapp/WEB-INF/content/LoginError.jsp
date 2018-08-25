<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
            
<html>
  <head><title>SecurePing Login Error Form</title></head>
  <body>
    <h1>Login Required -- Get It Right This Time!!!</h1>
    <form action="j_security_check" method="POST">
       <table border="0" width="30%" cellspacing="3" cellpadding="2">
          <tr>
             <td><b><label for="username">User Name</label></b></td>
             <td><input type="text" size="20" name="j_username"></td>
          </tr>
          <tr>
             <td><b><label for="password">Password</label></b></td>
             <td><input type="password" size="10" name="j_password" value="password1!"></td>
          </tr>
          <tr>
             <td><p><input type="submit" value="Login"></td> 
          </tr>
       </table>
    </form>
    <p/>
    Test accounts:
     <ul>
        <li>admin1/password1!</li>
        <li>user1/password1!</li>
        <li>known/password1! - someone who has a login, but no permissions</li>
     </ul>
  </body>
</html>
