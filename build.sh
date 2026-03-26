#!/bin/bash

# Set JAVA_HOME to Java 8
export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
export PATH=$JAVA_HOME/bin:$PATH

echo "Using Java 8 for Maven build:"
java -version

echo ""
echo "Building project (skipping tests for faster build)..."
mvn clean install -DskipTests
