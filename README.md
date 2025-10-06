# Endpoint Monitor

A self-hosted endpoint monitoring tool built with Spring Boot 3.5.x and Java 21, supporting curl (java-curl), ping (InetAddress.isReachable), and telnet (commons-net TelnetClient). Targets are stored in PostgreSQL 18, with checks run via Spring Scheduler and results saved in a `check_results` table. Liquibase manages schema migrations, and tests use TestRestTemplate and TestContainers.

## Badges

[TODO]

## Database Setup

### Bootstrap SQL
Initialize the schema with `src/main/resources/db/changelog/changelog-000-bootstrap.sql`:
```sql
CREATE TABLE targets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    endpoint TEXT NOT NULL,
    method VARCHAR(10) NOT NULL CHECK (method IN ('PING', 'CURL', 'TELNET')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    enabled BOOLEAN DEFAULT true,
    description VARCHAR(255),
    timeout BIGINT
);

CREATE TABLE check_results (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    target_id UUID NOT NULL REFERENCES targets(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_check_results_target_id ON check_results (target_id);
```

### Liquibase Migrations
Schema changes are managed with Liquibase YAML files, applied automatically on startup or manually.

#### Automated Migrations
Liquibase runs on Spring Boot startup:
- Config (`application.properties`):
  ```properties
  spring.liquibase.enabled=true
  spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.yaml
  spring.datasource.url=jdbc:postgresql://localhost:5432/endpoint_monitor
  spring.datasource.username=your_user
  spring.datasource.password=your_password
  ```

#### Manual Migrations
Run manually for development or debugging:
1. Configure `liquibase-maven-plugin` in `pom.xml`:
   ```xml
   <plugin>
     <groupId>org.liquibase</groupId>
     <artifactId>liquibase-maven-plugin</artifactId>
     <version>4.29.1</version>
     <configuration>
       <changeLogFile>src/main/resources/db/changelog/db.changelog-master.yaml</changeLogFile>
       <url>jdbc:postgresql://localhost:5432/endpoint_monitor</url>
       <username>your_user</username>
       <password>your_password</password>
     </configuration>
   </plugin>
   ```
2. Apply migrations:
   ```bash
   mvn liquibase:update
   ```
3. Other commands:
    - Check pending changesets: `mvn liquibase:status`
    - Roll back last changeset: `mvn liquibase:rollback -Dliquibase.rollbackCount=1`

## Testing

### Local
Run unit and integration tests:
```bash
./mvnw clean install test
```
- Uses TestRestTemplate for REST API tests (`MonitorControllerV1Test.java`) and TestContainers for PostgreSQL integration.
- Tests validate endpoint creation, check execution, and result storage.

### CI/CD
Run tests and verification in CI/CD (e.g., GitHub Actions):
```bash
./mvnw clean install verify
```
- Includes coverage reports (JaCoCo) and integration tests with TestContainers.
- Targets 80%+ coverage for controllers, services, and repositories.

## Setup
1. Install PostgreSQL 18 and create database `endpoint_monitor`.
2. Run `db/changelog/changelog-000-bootstrap.sql`.
3. Configure `application.properties` with DB credentials.
4. Start the app (applies Liquibase migrations):
   ```bash
   mvn spring-boot:run
   ```
5. Run tests:
   ```bash
   ./mvnw clean install test
   ```

## Future Plans
- Migrate `check_results` to TimescaleDB for time-series optimization.
- Add alerting (e.g., email on check failures) and Grafana dashboards.
- Implement WebUI for viewing targets and results.
