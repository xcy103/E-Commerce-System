#!/bin/bash

# Set JAVA_HOME to Java 8
export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
export PATH=$JAVA_HOME/bin:$PATH

echo "=========================================="
echo "Starting E-Commerce System"
echo "=========================================="
echo ""
echo "Using Java 8:"
java -version
echo ""
echo "Starting Spring Boot application..."
echo "Access the application at:"
echo "  - API: http://localhost:8080/api"
echo "  - Swagger UI: http://localhost:8080/api/swagger-ui.html"
echo ""
echo "Press Ctrl+C to stop the application"
echo ""

mvn spring-boot:run
