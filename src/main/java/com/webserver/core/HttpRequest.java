package com.webserver.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses raw HTTP request into a usable object
 * This is what Tomcat does when it receives a request!
 */
public class HttpRequest {

    private String method; // GET, POST, etc.
    private String uri; // /hello?name=John
    private String path; // /hello
    private String queryString; // name=John
    private String protocol; // HTTP/1.1
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> parameters = new HashMap<>();
    private String body;

    /**
     * Parse raw HTTP request from input stream
     * 
     * Example HTTP request:
     * GET /hello?name=John HTTP/1.1
     * Host: localhost:8080
     * User-Agent: curl/7.68.0
     * 
     * [blank line]
     * [optional body]
     */
    public HttpRequest(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        // Parse request line: "GET /hello?name=John HTTP/1.1"
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("Empty request");
        }

        System.out.println("[HttpRequest] Parsing: " + requestLine);

        String[] parts = requestLine.split(" ");
        if (parts.length != 3) {
            throw new IOException("Invalid request line: " + requestLine);
        }

        this.method = parts[0];
        this.uri = parts[1];
        this.protocol = parts[2];

        // Split path and query string
        int questionMark = uri.indexOf('?');
        if (questionMark >= 0) {
            this.path = uri.substring(0, questionMark);
            this.queryString = uri.substring(questionMark + 1);
            parseQueryString(queryString);
        } else {
            this.path = uri;
        }

        // Parse headers
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            int colon = line.indexOf(':');
            if (colon > 0) {
                String headerName = line.substring(0, colon).trim();
                String headerValue = line.substring(colon + 1).trim();
                headers.put(headerName.toLowerCase(), headerValue);
                System.out.println("[HttpRequest] Header: " + headerName + " = " + headerValue);
            }
        }

        // Parse body if present (for POST requests)
        if ("POST".equalsIgnoreCase(method) && headers.containsKey("content-length")) {
            int contentLength = Integer.parseInt(headers.get("content-length"));
            char[] bodyChars = new char[contentLength];
            reader.read(bodyChars, 0, contentLength);
            this.body = new String(bodyChars);
            System.out.println("[HttpRequest] Body: " + body);
        }
    }

    private void parseQueryString(String query) {
        if (query == null || query.isEmpty()) {
            return;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int eq = pair.indexOf('=');
            if (eq > 0) {
                String key = pair.substring(0, eq);
                String value = pair.substring(eq + 1);
                parameters.put(key, value);
            }
        }
    }

    // Getters
    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getPath() {
        return path;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHeader(String name) {
        return headers.get(name.toLowerCase());
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return method + " " + uri + " " + protocol;
    }
}
