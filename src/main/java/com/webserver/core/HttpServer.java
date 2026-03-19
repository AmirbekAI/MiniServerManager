package com.webserver.core;

import com.webserver.examples.AuthFilter;
import com.webserver.examples.HelloServlet;
import com.webserver.examples.LoggingFilter;
import com.webserver.examples.UserServlet;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The main HTTP server
 * This is like Tomcat's Connector - it accepts TCP connections
 * and hands them to the ServletContainer for processing
 */
public class HttpServer {

    private static final int PORT = 8000;
    private ServletContainer container;
    private boolean running = false;

    public HttpServer() {
        this.container = new ServletContainer();
    }

    /**
     * Start the server
     */
    public void start() throws IOException {
        // Initialize the container with servlets and filters
        setupContainer();

        ServerSocket serverSocket = new ServerSocket(PORT);
        running = true;

        System.out.println("========================================");
        System.out.println("  Minimal Java Web Server Started");
        System.out.println("  Port: " + PORT);
        System.out.println("========================================");
        System.out.println("\nTry these URLs:");
        System.out.println("  http://localhost:8080/hello");
        System.out.println("  http://localhost:8080/hello?name=YourName");
        System.out.println("  http://localhost:8080/user");
        System.out.println("\nPress Ctrl+C to stop\n");

        // Accept connections in a loop
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[Server] New connection from: " +
                        clientSocket.getInetAddress().getHostAddress());

                // Handle request (in a real server, this would be in a thread pool)
                handleConnection(clientSocket);

            } catch (IOException e) {
                if (running) {
                    System.err.println("[Server] Error accepting connection: " + e.getMessage());
                }
            }
        }

        serverSocket.close();
        container.destroy();
    }

    /**
     * Handle a single client connection
     */
    private void handleConnection(Socket socket) {
        try {
            // Parse the HTTP request
            HttpRequest request = new HttpRequest(socket.getInputStream());

            // Create the HTTP response
            HttpResponse response = new HttpResponse(socket.getOutputStream());

            // Let the container handle it (filters + servlet)
            container.handleRequest(request, response);

        } catch (Exception e) {
            System.err.println("[Server] Error handling request: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }

    /**
     * Setup servlets and filters
     * This is like web.xml configuration in traditional Java web apps
     */
    private void setupContainer() {
        System.out.println("[Server] Setting up container...\n");

        // Add filters (they execute in the order added)
        container.addFilter(new LoggingFilter());
        container.addFilter(new AuthFilter());

        // Register servlets
        container.registerServlet("/hello", new HelloServlet());
        container.registerServlet("/user", new UserServlet());

        System.out.println();
    }

    /**
     * Main entry point
     */
    public static void main(String[] args) {
        try {
            HttpServer server = new HttpServer();
            server.start();
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
