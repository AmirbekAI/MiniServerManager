package com.webserver.servlet;

import com.webserver.core.HttpRequest;
import com.webserver.core.HttpResponse;

/**
 * Simplified version of javax.servlet.Servlet
 * This is the core interface that all servlets must implement
 */
public interface Servlet {
    
    /**
     * Called when the servlet is first loaded
     */
    void init();
    
    /**
     * Called for each HTTP request
     * This is where your business logic goes
     */
    void service(HttpRequest request, HttpResponse response) throws Exception;
    
    /**
     * Called when the servlet is being destroyed
     */
    void destroy();
}
