#!/bin/bash

# Quick Start Script for Kafka Order System
# This script automates the setup process

set -e  # Exit on error

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║                                                                ║"
echo "║       🚀 KAFKA ORDER SYSTEM - QUICK START SETUP 🚀             ║"
echo "║                                                                ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

# Check prerequisites
echo "📋 Checking prerequisites..."

# Check Docker
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    exit 1
fi
echo "✅ Docker found"

# Check Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven first."
    exit 1
fi
echo "✅ Maven found"

# Check Java
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17 or higher."
    exit 1
fi
echo "✅ Java found: $(java -version 2>&1 | head -n 1)"

echo ""
echo "🐳 Starting Docker containers..."
docker-compose up -d

echo ""
echo "⏳ Waiting for Kafka to be ready (20 seconds)..."
sleep 20

echo ""
echo "🔨 Building project with Maven..."
mvn clean install

echo ""
echo "╔════════════════════════════════════════════════════════════════╗"
echo "║                                                                ║"
echo "║              ✅ SETUP COMPLETE! YOU'RE ALL SET! ✅              ║"
echo "║                                                                ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""
echo "📝 Next steps:"
echo ""
echo "  1. Run the application:"
echo "     $ mvn spring-boot:run"
echo ""
echo "  2. In another terminal, send a test order:"
echo "     $ curl -X POST http://localhost:8080/api/orders \\"
echo "       -H 'Content-Type: application/json' \\"
echo "       -d '{\"user\": \"john@example.com\", \"item\": \"Laptop\"}'"
echo ""
echo "  3. Check the application logs to see the order being processed"
echo ""
echo "📍 Endpoints:"
echo "   - Health: http://localhost:8080/api/orders/health"
echo "   - Create Order: POST http://localhost:8080/api/orders"
echo ""
echo "📚 For more information, see README.md"
echo ""
