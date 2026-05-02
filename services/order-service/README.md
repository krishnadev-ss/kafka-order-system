# Order Service

This microservice produces `Order` events to the Kafka topic `orders`.

Build:
```bash
cd services/order-service
mvn -DskipTests clean package
```

Run (jar):
```bash
java -jar target/order-service-0.0.1-SNAPSHOT.jar
```

Or run with Spring Boot plugin:
```bash
mvn spring-boot:run
```

HTTP API

- POST /api/orders

Request JSON:
```json
{
  "orderId": "string (optional)",
  "user": "string",
  "item": "string"
}
```

Notes:
- Kafka must be available at `localhost:9092`.
- This service only produces events; there is no database here.
