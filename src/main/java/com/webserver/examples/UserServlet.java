package com.webserver.examples;

import com.webserver.core.HttpRequest;
import com.webserver.core.HttpResponse;
import com.webserver.servlet.Servlet;

/**
 * Example servlet that demonstrates authentication
 * This will be protected by AuthFilter
 */
public class UserServlet implements Servlet {

    @Override
    public void init() {
        System.out.println("[UserServlet] Initialized");
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        System.out.println("[UserServlet] Processing request");

        // If we got here, the AuthFilter allowed us through
        String username = request.getParameter("user");

        response.write("<html>");
        response.write("<head><title>User Profile</title></head>");
        response.write("<body style='font-family: Arial; padding: 20px;'>");
        response.write("<h1>User Profile</h1>");
        response.write("<p>Welcome, <b>" + username + "</b>!</p>");
        response.write("<p style='color: green;'>✓ You passed authentication</p>");
        response.write("<p>This servlet is protected by <code>AuthFilter</code></p>");
        response.write("<hr>");
        response.write("<p>Try without auth: <a href='/user'>/user</a> (will be blocked)</p>");
        response.write("<p>Try with auth: <a href='/user?user=admin&pass=secret'>/user?user=admin&pass=secret</a></p>");
        response.write("</body>");
        response.write("</html>");
    }

    @Override
    public void destroy() {
        System.out.println("[UserServlet] Destroyed");
    }
}
