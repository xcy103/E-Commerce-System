#!/bin/bash

echo "=========================================="
echo "E-Commerce System - Quick Start"
echo "=========================================="
echo ""

# Check Homebrew
echo "1. Checking Homebrew..."
if command -v brew &> /dev/null; then
    echo "✅ Homebrew is installed"
else
    echo "❌ Homebrew is not installed"
    echo "   Install from: https://brew.sh"
    exit 1
fi

echo ""

# Check Java 8
echo "2. Checking Java 8..."
JAVA8_HOME=$(/usr/libexec/java_home -v 1.8 2>/dev/null)
if [ ! -z "$JAVA8_HOME" ]; then
    echo "✅ Java 8 is available"
else
    echo "❌ Java 8 is not installed"
    echo "   Install via: brew install java8"
    exit 1
fi

echo ""

# Check Maven
echo "3. Checking Maven..."
if command -v mvn &> /dev/null; then
    echo "✅ Maven is installed"
else
    echo "❌ Maven is not installed"
    echo "   Install via: ./install-maven.sh or brew install maven"
    exit 1
fi

echo ""

# Check PostgreSQL
echo "4. Checking PostgreSQL..."
if command -v psql &> /dev/null; then
    echo "✅ PostgreSQL is installed"
    
    if brew services list 2>/dev/null | grep -q "postgresql.*started"; then
        echo "✅ PostgreSQL service is running"
    else
        echo "⚠️  PostgreSQL service is not running"
        echo "   Start via: brew services start postgresql@14"
    fi
    
    if psql -lqt 2>/dev/null | cut -d \| -f 1 | grep -qw ecommerce_db; then
        echo "✅ Database 'ecommerce_db' exists"
    else
        echo "⚠️  Database 'ecommerce_db' does not exist"
        echo "   Create via: psql postgres -c 'CREATE DATABASE ecommerce_db;'"
    fi
else
    echo "❌ PostgreSQL is not installed"
    echo "   Install via: brew install postgresql@14"
    exit 1
fi

echo ""

# Check Node.js
echo "5. Checking Node.js..."
if command -v node &> /dev/null; then
    echo "✅ Node.js is installed (includes npm)"
else
    echo "❌ Node.js is not installed"
    echo "   Install via: brew install node"
    exit 1
fi

echo ""
echo "=========================================="
echo "Ready to build and run!"
echo "=========================================="
