# Inventory Service

This service consumes `Order` events from the `orders` Kafka topic and reserves inventory in PostgreSQL.

Build:
```bash
cd services/inventory-service
mvn -DskipTests clean package
```

Run (jar):
```bash
java -jar target/inventory-service-0.0.1-SNAPSHOT.jar
```

Kafka must be available at `localhost:9092` and Postgres at `postgres:5432` as configured in `application.yml`.
