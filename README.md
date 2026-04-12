# Task Service

Production-style Spring Boot service (CRUD) with:
- Validation + consistent error responses
- Correlation ID (`X-Request-Id`) in logs + response
- Actuator health endpoints

## Requirements
- Java 17+
- Maven 3.9+

## Run locally
```bash
mvn clean package
mvn test 
mvn spring-boot:run
```
