# Minimal Java Web Server - Understanding Tomcat Internals

If you have ever worked with frameworks like Spring Boot and thought that they work like magic, then this project is for you. It demonstrates how servlet containers like Tomcat work under the hood. It's a simplified and a working version of a servlet container.

## What You'll Learn

1. **HTTP Protocol Handling** - How raw HTTP requests are parsed
2. **Servlet Container Architecture** - The core engine managing servlets
3. **Filter Chain Pattern** - How filters intercept requests before servlets
4. **Request/Response Lifecycle** - How data flows through the system

## Project Structure

```
src/main/java/com/webserver/
├── core/
│   ├── HttpServer.java           # Main server (accepts connections)
│   ├── HttpRequest.java           # Parses raw HTTP into usable format
│   ├── HttpResponse.java          # Builds HTTP responses
│   └── ServletContainer.java      # Manages servlets and filters
├── servlet/
│   ├── Servlet.java               # Interface (like javax.servlet.Servlet)
│   └── Filter.java                # Interface for filters
└── examples/
    ├── HelloServlet.java          # Example servlet
    ├── LoggingFilter.java         # Example filter
    └── AuthFilter.java            # Example authentication filter
```

## How to Run

```bash
# Compile
javac -d bin src/main/java/com/webserver/**/*.java

# Run
java -cp bin com.webserver.core.HttpServer

# Test
curl http://localhost:8080/hello
```

## Key Concepts Demonstrated

### 1. Filter Chain
Filters wrap around servlets, allowing pre/post processing:
```
Request → Filter1 → Filter2 → Servlet → Filter2 → Filter1 → Response
```

### 2. Request Flow
1. Server accepts TCP connection
2. Parse HTTP request into HttpRequest object
3. Container finds matching servlet
4. Execute filter chain
5. Servlet processes request
6. Build and send HttpResponse
