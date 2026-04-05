## 🧪 Testing Guide for Kafka Order System

This document provides comprehensive testing scenarios and instructions for validating the Kafka Order System.

### Prerequisites

Before testing, ensure:
1. ✅ Kafka and Zookeeper are running: `docker-compose ps`
2. ✅ Spring Boot application is running: `mvn spring-boot:run`
3. ✅ Application health is OK: `curl http://localhost:8080/api/orders/health`

---

## 📋 Test Scenarios

### Scenario 1: Health Check (Sanity Test)

**Objective:** Verify the service is running and responding

**Steps:**
```bash
curl -X GET http://localhost:8080/api/orders/health
```

**Expected Response:**
```json
{
  "status": "✅ Order Service is running",
  "timestamp": "1704067200000"
}
```

**Expected Status Code:** `200 OK`

---

### Scenario 2: Create Single Order

**Objective:** Send a single order and verify it's created

**Command:**
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "ORD-001",
    "user": "john@example.com",
    "item": "Laptop"
  }'
```

**Expected Response:**
```json
{
  "status": "success",
  "message": "Order received and sent to processing queue",
  "orderId": "ORD-001",
  "user": "john@example.com",
  "item": "Laptop",
  "timestamp": 1704067200000,
  "orderStatus": "PENDING"
}
```

**Expected Status Code:** `202 Accepted`

**Application Logs Should Show:**
```
📤 Preparing to send order: ORD-001
✅ Order sent successfully!
   Order ID: ORD-001
   Topic: orders
   Partition: 0
   Offset: 0

📨 ORDER CONSUMER TRIGGERED
📥 Received order: ORD-001
   User: john@example.com
   Item: Laptop
⏳ Processing order...
✅ Order processed successfully!
```

---

### Scenario 3: Auto-generated Order ID

**Objective:** Verify order ID is auto-generated when not provided

**Command:**
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "user": "alice@example.com",
    "item": "Phone"
  }'
```

**Expected Response:**
```json
{
  "status": "success",
  "message": "Order received and sent to processing queue",
  "orderId": "ORD-ABC12345",  // Auto-generated UUID
  "user": "alice@example.com",
  "item": "Phone",
  "timestamp": 1704067200000,
  "orderStatus": "PENDING"
}
```

**Validation:**
- `orderId` should match pattern `ORD-[A-Z0-9]{8}`
- Response status code should be `202 Accepted`

---

### Scenario 4: Concurrent Order Processing

**Objective:** Verify multiple orders are processed concurrently

**Command:**
```bash
# Send 5 orders in quick succession
for i in {1..5}; do
  curl -X POST http://localhost:8080/api/orders \
    -H "Content-Type: application/json" \
    -d "{
      \"user\": \"user$i@example.com\",
      \"item\": \"Product-$i\"
    }" &
done
wait
```

**Expected Behavior:**
- All requests should return `202 Accepted`
- Application logs should show concurrent processing (up to 3 at a time per config)
- All orders should be received and processed

**Timing Validation:**
- With 3 concurrent consumers, 5 orders should complete in ~2-3 seconds
- Without concurrency, it would take ~5+ seconds (1 second per order)

---

### Scenario 5: Error Handling - Missing Required Fields

#### Test 5a: Missing User Field

**Command:**
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "ORD-ERROR",
    "item": "Laptop"
  }'
```

**Expected Response:**
```json
{
  "status": "error",
  "message": "User field is required",
  "timestamp": 1704067200000
}
```

**Expected Status Code:** `400 Bad Request`

#### Test 5b: Missing Item Field

**Command:**
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "ORD-ERROR",
    "user": "john@example.com"
  }'
```

**Expected Response:**
```json
{
  "status": "error",
  "message": "Item field is required",
  "timestamp": 1704067200000
}
```

**Expected Status Code:** `400 Bad Request`

---

### Scenario 6: Invalid JSON

**Objective:** Verify proper error handling for malformed JSON

**Command:**
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{ invalid json }'
```

**Expected Response:** `400 Bad Request` with error details

---

### Scenario 7: Get Order Status

**Objective:** Retrieve status for a specific order

**Command:**
```bash
curl -X GET http://localhost:8080/api/orders/ORD-001
```

**Expected Response:**
```json
{
  "orderId": "ORD-001",
  "status": "PROCESSING",
  "message": "Order is being processed by the system"
}
```

**Expected Status Code:** `200 OK`

---

### Scenario 8: Kafka Topic Verification

**Objective:** Verify messages are stored in Kafka topic

**Commands:**

**List all topics:**
```bash
docker exec kafka-broker kafka-topics --list --bootstrap-server localhost:9092
```

Expected output includes: `orders`

**View topic details:**
```bash
docker exec kafka-broker kafka-topics --describe --topic orders --bootstrap-server localhost:9092
```

Expected output:
```
Topic: orders       PartitionCount: 3       ReplicationFactor: 1
Topic: orders       Partition: 0    Leader: 1       Replicas: 1       Isr: 1
Topic: orders       Partition: 1    Leader: 1       Replicas: 1       Isr: 1
Topic: orders       Partition: 2    Leader: 1       Replicas: 1       Isr: 1
```

**Read messages from topic:**
```bash
docker exec kafka-broker kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic orders \
  --from-beginning \
  --max-messages 5
```

---

### Scenario 9: Consumer Group Status

**Objective:** Verify consumer group and offset tracking

**Commands:**

**List consumer groups:**
```bash
docker exec kafka-broker kafka-consumer-groups --list --bootstrap-server localhost:9092
```

Expected output should include: `order-consumer-group`

**View group details:**
```bash
docker exec kafka-broker kafka-consumer-groups --describe \
  --group order-consumer-group \
  --bootstrap-server localhost:9092
```

Expected output shows offsets, lag, etc.

---

## 🧬 Load Testing

### Stress Test: 100 Orders

**Objective:** Verify performance with high load

**Script:**
```bash
#!/bin/bash

total=100
for i in $(seq 1 $total); do
  curl -s -X POST http://localhost:8080/api/orders \
    -H "Content-Type: application/json" \
    -d "{
      \"user\": \"load-test-$i@example.com\",
      \"item\": \"Product-$i\"
    }" &
  
  # Limit concurrent requests to 10
  if [ $((i % 10)) -eq 0 ]; then
    wait
  fi
done
wait

echo "✅ Sent $total orders"
```

**Expected Results:**
- All requests return `202 Accepted`
- No errors in logs
- All orders processed within reasonable time
- Memory usage remains stable

---

## 📊 Logging Verification

### Check Application Logs

**With Maven:**
```bash
mvn spring-boot:run 2>&1 | grep -E "ORDER|Order|KAFKA|Kafka"
```

**With JAR:**
```bash
java -jar target/kafka-order-system-1.0.0.jar 2>&1 | tail -100
```

### Expected Log Patterns

1. **Order Creation:**
   ```
   📨 Received order creation request for user: john@example.com
   ✅ Order validated. Sending to Kafka topic...
   ```

2. **Producer:**
   ```
   📤 Preparing to send order: ORD-001
   ✅ Order sent successfully!
   ```

3. **Consumer:**
   ```
   📨 ORDER CONSUMER TRIGGERED
   📥 Received order: ORD-001
   ⏳ Processing order...
   ✅ Order processed successfully!
   ```

---

## 🔍 Debugging Tips

### Enable Debug Logging

Edit `application.yml`:
```yaml
logging:
  level:
    com.example.kafkaordersystem: DEBUG
    org.springframework.kafka: DEBUG
```

### Monitor Kafka Broker Logs

```bash
docker logs -f kafka-broker
```

### Check Network Connectivity

```bash
# From host to Kafka
nc -zv localhost 9092
ping kafka-broker  # If using Docker network
```

### Verify JSON Serialization

The consumer should auto-deserialize Order objects:
```
✅ Order(
    orderId=ORD-001,
    user=john@example.com,
    item=Laptop,
    timestamp=1704067200000,
    status=PENDING
)
```

---

## ✅ Test Checklist

- [ ] Health check returns 200 OK
- [ ] Single order creates successfully
- [ ] Auto-generated order IDs work
- [ ] Errors handled properly (400 for missing fields)
- [ ] Concurrent processing works (3 orders at a time)
- [ ] Consumer receives and processes messages
- [ ] Kafka topic contains messages
- [ ] Consumer group offset advances
- [ ] Logs show complete flow
- [ ] Load test (100 orders) completes
- [ ] No errors in logs
- [ ] Memory usage stable

---

## 🛠️ Using Provided Test Scripts

### API Test Script

```bash
bash test-api.sh
```

This script runs 8 different test scenarios automatically.

### Quick Tests with curl

```bash
# Health check
curl http://localhost:8080/api/orders/health | jq

# Create order
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"user":"test@example.com","item":"Test"}' | jq

# Get status
curl http://localhost:8080/api/orders/ORD-TEST | jq
```

---

## 📈 Performance Metrics to Monitor

- **Response Time:** Should be < 100ms for API
- **Throughput:** > 100 orders/second
- **Consumer Lag:** Should be near 0
- **Error Rate:** Should be 0% for valid requests
- **Memory Usage:** Stable, < 500MB
- **CPU Usage:** Moderate (varies with load)

---

## 🚩 Common Issues

| Issue | Cause | Solution |
|-------|-------|----------|
| Connection refused | Kafka not running | `docker-compose up -d` |
| Port already in use | Port 8080 occupied | Change port in `application.yml` |
| Messages not received | Consumer not running | Check application logs |
| Offset not advancing | Consumer group issue | Check consumer group details |
| JSON parse error | Invalid JSON format | Validate JSON structure |
| Authorization error | Kafka security | Check Kafka configuration |

---

For more help, check the main [README.md](README.md).
