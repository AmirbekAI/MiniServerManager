package com.webserver.core;

import com.webserver.servlet.Filter;
import com.webserver.servlet.FilterChain;
import com.webserver.servlet.Servlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the HEART of how Tomcat works!
 * 
 * The ServletContainer:
 * 1. Manages servlet lifecycle (init, service, destroy)
 * 2. Maps URLs to servlets
 * 3. Builds and executes the filter chain
 * 4. Handles request dispatching
 */
public class ServletContainer {

    // Map URL patterns to servlets
    private Map<String, Servlet> servletMappings = new HashMap<>();

    // List of filters (applied to all requests in this simple version)
    private List<Filter> filters = new ArrayList<>();

    /**
     * Register a servlet for a specific URL pattern
     */
    public void registerServlet(String urlPattern, Servlet servlet) {
        System.out.println("[Container] Registering servlet: " + urlPattern +
                " -> " + servlet.getClass().getSimpleName());
        servlet.init();
        servletMappings.put(urlPattern, servlet);
    }

    /**
     * Add a filter to the chain
     * In real Tomcat, filters can have URL patterns too
     */
    public void addFilter(Filter filter) {
        System.out.println("[Container] Adding filter: " + filter.getClass().getSimpleName());
        filter.init();
        filters.add(filter);
    }

    /**
     * Process an incoming request
     * This is where the magic happens!
     */
    public void handleRequest(HttpRequest request, HttpResponse response) {
        try {
            System.out.println("\n[Container] ========== Processing Request ==========");
            System.out.println("[Container] " + request);

            // Find the servlet for this path
            Servlet servlet = findServlet(request.getPath());

            if (servlet == null) {
                response.setStatus(404, "Not Found");
                response.write("<html><body><h1>404 - Not Found</h1><p>No servlet mapped to: " +
                        request.getPath() + "</p></body></html>");
                response.send();
                return;
            }

            System.out.println("[Container] Found servlet: " + servlet.getClass().getSimpleName());

            // Build the filter chain
            // This is the KEY concept! The chain wraps filters around the servlet
            FilterChain chain = buildFilterChain(servlet);

            // Execute the chain (filters + servlet)
            System.out.println("[Container] Executing filter chain...");
            chain.doFilter(request, response);

            // Send the response
            response.send();

            System.out.println("[Container] ========== Request Complete ==========\n");

        } catch (Exception e) {
            System.err.println("[Container] Error processing request: " + e.getMessage());
            e.printStackTrace();
            try {
                response.setStatus(500, "Internal Server Error");
                response.write("<html><body><h1>500 - Internal Server Error</h1><p>" +
                        e.getMessage() + "</p></body></html>");
                response.send();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Find servlet matching the request path
     */
    private Servlet findServlet(String path) {
        // Simple exact match (real Tomcat supports wildcards like /api/*)
        return servletMappings.get(path);
    }

    /**
     * Build the filter chain
     * 
     * This creates a chain like:
     * Filter1 -> Filter2 -> Filter3 -> Servlet
     * 
     * Each filter can call chain.doFilter() to continue,
     * or stop the chain by not calling it
     */
    private FilterChain buildFilterChain(Servlet servlet) {
        // Start with the servlet at the end
        FilterChain chain = new FilterChain() {
            @Override
            public void doFilter(HttpRequest request, HttpResponse response) throws Exception {
                System.out.println("[FilterChain] Reached end of chain, invoking servlet");
                servlet.service(request, response);
            }
        };

        // Wrap each filter around the chain (in reverse order)
        // This is the clever part!
        for (int i = filters.size() - 1; i >= 0; i--) {
            Filter filter = filters.get(i);
            FilterChain currentChain = chain;

            chain = new FilterChain() {
                @Override
                public void doFilter(HttpRequest request, HttpResponse response) throws Exception {
                    System.out.println("[FilterChain] Executing filter: " + filter.getClass().getSimpleName());
                    filter.doFilter(request, response, currentChain);
                }
            };
        }

        return chain;
    }

    /**
     * Shutdown the container
     */
    public void destroy() {
        System.out.println("[Container] Shutting down...");

        for (Filter filter : filters) {
            filter.destroy();
        }

        for (Servlet servlet : servletMappings.values()) {
            servlet.destroy();
        }
    }
}
