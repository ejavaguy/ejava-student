package info.ejava.examples.ejb.cdisales.web;

import java.io.IOException;
import java.io.PrintWriter;

import javaeetutorial.hello1.Hello;

import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/hello")
public class CatalogServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Inject Hello hello;
    
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setBufferSize(8192);
        try (PrintWriter out = response.getWriter()) {
            out.println("<html lang=\"en\">"
                    + "<head><title>Servlet Hello</title></head>");

            // then write the data of the response
            out.println("<body  bgcolor=\"#ffffff\">"
                    + "<form method=\"get\">"
                    + "<h2>Hello, my name is Duke. What's yours?</h2>"
                    + "<input title=\"My name is: \"type=\"text\" "
                    + "name=\"username\" size=\"25\">" + "<p></p>"
                    + "<input type=\"submit\" value=\"Submit\">"
                    + "<input type=\"reset\" value=\"Reset\">" + "</form>");

            String username = request.getParameter("username");
            hello.setName(username);
            
            if (username != null && username.length() > 0) {
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/response.xhtml");

                if (dispatcher != null) {
                    dispatcher.include(request, response);
                }
            }
            out.println("</body></html>");
        }
    }
}
