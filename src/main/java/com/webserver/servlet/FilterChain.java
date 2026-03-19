package com.webserver.servlet;

import com.webserver.core.HttpRequest;
import com.webserver.core.HttpResponse;

/**
 * The FilterChain is the KEY to understanding how filters work!
 * 
 * It's a chain of responsibility pattern:
 * - Each filter calls chain.doFilter() to pass control to the next filter
 * - The last item in the chain is the actual servlet
 * - This allows filters to do work BEFORE and AFTER the servlet executes
 */
public interface FilterChain {

    /**
     * Continue processing the request
     * - If there are more filters, invoke the next filter
     * - If no more filters, invoke the servlet
     */
    void doFilter(HttpRequest request, HttpResponse response) throws Exception;
}
