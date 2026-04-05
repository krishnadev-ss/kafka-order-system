#!/bin/bash

# Test Script for Kafka Order System
# This script tests the API endpoints with example requests

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║                                                                ║"
echo "║       🧪 KAFKA ORDER SYSTEM - API TEST SCRIPT 🧪              ║"
echo "║                                                                ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

# Configuration
BASE_URL="http://localhost:8080/api"
ORDERS_ENDPOINT="$BASE_URL/orders"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Test 1: Health Check
echo -e "${BLUE}📋 Test 1: Health Check${NC}"
echo "GET $ORDERS_ENDPOINT/health"
echo ""
curl -s -X GET "$ORDERS_ENDPOINT/health" | jq .
echo ""
echo ""

# Test 2: Create Order 1
echo -e "${BLUE}📋 Test 2: Create Order (Alice)${NC}"
echo "POST $ORDERS_ENDPOINT"
echo ""
ORDER_ID_1=$(uuidgen | cut -c1-8)
curl -s -X POST "$ORDERS_ENDPOINT" \
  -H "Content-Type: application/json" \
  -d "{
    \"orderId\": \"ORD-$ORDER_ID_1\",
    \"user\": \"alice@example.com\",
    \"item\": \"Laptop\"
  }" | jq .
echo ""
echo ""

# Wait a moment for processing
sleep 2

# Test 3: Create Order 2
echo -e "${BLUE}📋 Test 3: Create Order (Bob)${NC}"
echo "POST $ORDERS_ENDPOINT"
echo ""
ORDER_ID_2=$(uuidgen | cut -c1-8)
curl -s -X POST "$ORDERS_ENDPOINT" \
  -H "Content-Type: application/json" \
  -d "{
    \"orderId\": \"ORD-$ORDER_ID_2\",
    \"user\": \"bob@example.com\",
    \"item\": \"Phone\"
  }" | jq .
echo ""
echo ""

# Wait a moment for processing
sleep 2

# Test 4: Create Order 3
echo -e "${BLUE}📋 Test 4: Create Order (Charlie)${NC}"
echo "POST $ORDERS_ENDPOINT"
echo ""
ORDER_ID_3=$(uuidgen | cut -c1-8)
curl -s -X POST "$ORDERS_ENDPOINT" \
  -H "Content-Type: application/json" \
  -d "{
    \"orderId\": \"ORD-$ORDER_ID_3\",
    \"user\": \"charlie@example.com\",
    \"item\": \"Tablet\"
  }" | jq .
echo ""
echo ""

# Test 5: Create Order with Auto-generated ID
echo -e "${BLUE}📋 Test 5: Create Order with Auto-generated ID${NC}"
echo "POST $ORDERS_ENDPOINT"
echo ""
curl -s -X POST "$ORDERS_ENDPOINT" \
  -H "Content-Type: application/json" \
  -d "{
    \"user\": \"diana@example.com\",
    \"item\": \"Monitor\"
  }" | jq .
echo ""
echo ""

# Test 6: Get Order Status
echo -e "${BLUE}📋 Test 6: Get Order Status${NC}"
echo "GET $ORDERS_ENDPOINT/ORD-$ORDER_ID_1"
echo ""
curl -s -X GET "$ORDERS_ENDPOINT/ORD-$ORDER_ID_1" | jq .
echo ""
echo ""

# Test 7: Error Handling - Missing required field
echo -e "${BLUE}📋 Test 7: Error Handling - Missing User Field${NC}"
echo "POST $ORDERS_ENDPOINT (should fail)"
echo ""
curl -s -X POST "$ORDERS_ENDPOINT" \
  -H "Content-Type: application/json" \
  -d "{
    \"orderId\": \"ORD-TEST\",
    \"item\": \"Monitor\"
  }" | jq .
echo ""
echo ""

# Test 8: Error Handling - Invalid JSON
echo -e "${BLUE}📋 Test 8: Error Handling - Invalid JSON${NC}"
echo "POST $ORDERS_ENDPOINT (should fail)"
echo ""
curl -s -X POST "$ORDERS_ENDPOINT" \
  -H "Content-Type: application/json" \
  -d "{ invalid json }" | jq .
echo ""
echo ""

echo -e "${GREEN}✅ API Tests Complete!${NC}"
echo ""
echo -e "${YELLOW}📝 Notes:${NC}"
echo "  1. Check application logs to see consumer processing the messages"
echo "  2. Look for the consumer output showing order received and processing"
echo "  3. Multiple orders should be processed concurrently (3 at a time)"
echo ""
