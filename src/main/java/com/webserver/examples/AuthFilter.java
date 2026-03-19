package com.webserver.examples;

import com.webserver.core.HttpRequest;
import com.webserver.core.HttpResponse;
import com.webserver.servlet.Filter;
import com.webserver.servlet.FilterChain;

/**
 * Example authentication filter
 * 
 * This demonstrates how filters can BLOCK requests
 * by NOT calling chain.doFilter()
 */
public class AuthFilter implements Filter {

    @Override
    public void init() {
        System.out.println("[AuthFilter] Initialized");
    }

    @Override
    public void doFilter(HttpRequest request, HttpResponse response, FilterChain chain) throws Exception {
        System.out.println("[AuthFilter] Checking authentication for: " + request.getPath());

        // Only protect /user path
        if (!request.getPath().equals("/user")) {
            System.out.println("[AuthFilter] Path not protected, allowing through");
            chain.doFilter(request, response);
            return;
        }

        // Check for credentials
        String username = request.getParameter("user");
        String password = request.getParameter("pass");

        if (username != null && password != null &&
                username.equals("admin") && password.equals("secret")) {

            System.out.println("[AuthFilter] ✓ Authentication successful for: " + username);
            // Allow the request to continue
            chain.doFilter(request, response);

        } else {
            System.out.println("[AuthFilter] ✗ Authentication failed!");

            // BLOCK the request - don't call chain.doFilter()
            // Send error response instead
            response.setStatus(401, "Unauthorized");
            response.write("<html>");
            response.write("<head><title>401 Unauthorized</title></head>");
            response.write("<body style='font-family: Arial; padding: 20px;'>");
            response.write("<h1>401 - Unauthorized</h1>");
            response.write("<p style='color: red;'>Authentication required!</p>");
            response.write("<p>This request was blocked by <code>AuthFilter</code></p>");
            response.write("<p>Try: <a href='/user?user=admin&pass=secret'>/user?user=admin&pass=secret</a></p>");
            response.write("</body>");
            response.write("</html>");

            // Notice: we DON'T call chain.doFilter()
            // This stops the request here - the servlet never runs!
        }
    }

    @Override
    public void destroy() {
        System.out.println("[AuthFilter] Destroyed");
    }
}
