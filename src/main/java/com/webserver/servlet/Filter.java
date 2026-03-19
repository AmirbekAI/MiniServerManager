package com.webserver.servlet;

import com.webserver.core.HttpRequest;
import com.webserver.core.HttpResponse;

/**
 * Simplified version of javax.servlet.Filter
 * Filters intercept requests before they reach servlets
 */
public interface Filter {

    /**
     * Called when the filter is initialized
     */
    void init();

    /**
     * Called for each request that matches this filter
     * 
     * @param request  The HTTP request
     * @param response The HTTP response
     * @param chain    The filter chain to continue processing
     */
    void doFilter(HttpRequest request, HttpResponse response, FilterChain chain) throws Exception;

    /**
     * Called when the filter is destroyed
     */
    void destroy();
}
