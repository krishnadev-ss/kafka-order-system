# Notification Service

This service consumes `Order` events and sends notifications (simulated).

Build:
```bash
cd services/notification-service
mvn -DskipTests clean package
```

Run (jar):
```bash
java -jar target/notification-service-0.0.1-SNAPSHOT.jar
```

Kafka must be available at `localhost:9092`.
