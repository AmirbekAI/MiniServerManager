package com.webserver.core;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Builds HTTP responses
 * This is what Tomcat uses to send data back to the client
 */
public class HttpResponse {

    private OutputStream output;
    private int statusCode = 200;
    private String statusMessage = "OK";
    private Map<String, String> headers = new HashMap<>();
    private StringBuilder body = new StringBuilder();

    public HttpResponse(OutputStream output) {
        this.output = output;
        // Default headers
        headers.put("Server", "MinimalJavaServer/1.0");
        headers.put("Content-Type", "text/html; charset=UTF-8");
    }

    public void setStatus(int code, String message) {
        this.statusCode = code;
        this.statusMessage = message;
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public PrintWriter getWriter() {
        return new PrintWriter(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                body.append((char) b);
            }
        });
    }

    public void write(String content) {
        body.append(content);
    }

    /**
     * Send the complete HTTP response
     * 
     * Format:
     * HTTP/1.1 200 OK
     * Content-Type: text/html
     * Content-Length: 123
     * 
     * <html>...</html>
     */
    public void send() throws IOException {
        String bodyContent = body.toString();

        // Set content length
        headers.put("Content-Length", String.valueOf(bodyContent.getBytes().length));

        // Build response
        StringBuilder response = new StringBuilder();

        // Status line
        response.append("HTTP/1.1 ").append(statusCode).append(" ").append(statusMessage).append("\r\n");

        // Headers
        for (Map.Entry<String, String> header : headers.entrySet()) {
            response.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }

        // Blank line separates headers from body
        response.append("\r\n");

        // Body
        response.append(bodyContent);

        // Send to client
        output.write(response.toString().getBytes());
        output.flush();

        System.out.println("[HttpResponse] Sent: " + statusCode + " " + statusMessage +
                " (" + bodyContent.length() + " bytes)");
    }
}
