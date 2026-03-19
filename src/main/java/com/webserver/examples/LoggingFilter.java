package com.webserver.examples;

import com.webserver.core.HttpRequest;
import com.webserver.core.HttpResponse;
import com.webserver.servlet.Filter;
import com.webserver.servlet.FilterChain;

/**
 * Example filter that logs requests
 * 
 * This demonstrates:
 * 1. Pre-processing (before servlet)
 * 2. Calling chain.doFilter() to continue
 * 3. Post-processing (after servlet)
 */
public class LoggingFilter implements Filter {

    @Override
    public void init() {
        System.out.println("[LoggingFilter] Initialized");
    }

    @Override
    public void doFilter(HttpRequest request, HttpResponse response, FilterChain chain) throws Exception {
        // PRE-PROCESSING: Before the servlet runs
        long startTime = System.currentTimeMillis();
        System.out.println("[LoggingFilter] >>> REQUEST START: " + request.getMethod() + " " + request.getPath());

        // Continue the chain (next filter or servlet)
        // This is the KEY line! Without it, the request stops here
        chain.doFilter(request, response);

        // POST-PROCESSING: After the servlet completes
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("[LoggingFilter] <<< REQUEST END: " + request.getPath() + " (" + duration + "ms)");
    }

    @Override
    public void destroy() {
        System.out.println("[LoggingFilter] Destroyed");
    }
}
