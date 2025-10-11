# Endpoint Monitor

A self-hosted endpoint monitoring tool built with Spring Boot 3.5.x and Java 21, supporting curl (java-curl), ping (InetAddress.isReachable), and telnet (commons-net TelnetClient). Targets are stored in PostgreSQL 18, with checks run via Spring Scheduler and results saved in a `check_results` table. Liquibase manages schema migrations, and tests use TestRestTemplate and TestContainers.

## Badges

[TODO]

## Database Setup

### Bootstrap SQL

Initialize the schema with `src/main/resources/db/changelog/changelog-000-bootstrap.sql`:

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

## TSDB for Check Result Logs

InfluxDB Integration - Local Testing

Run influxdb docker
```
docker run -p 8086:8086 \
    -e DOCKER_INFLUXDB_INIT_MODE=setup \
    -e DOCKER_INFLUXDB_INIT_USERNAME=admin \
    -e DOCKER_INFLUXDB_INIT_PASSWORD=adminpassword \
    -e DOCKER_INFLUXDB_INIT_ORG=myorg \
    -e DOCKER_INFLUXDB_INIT_BUCKET=monitoring \
    influxdb:2.7
```
```
    ...
    -e INFLUXDB_HTTP_AUTH_ENABLED=false \
    ...
```

Generate Token
Navigate to `http://localhost:8086:8086`, login and generate new token

Update `application-local.yaml` or just add environment variable

```
influx:
  token: <token>
```

Run Application

```
./mvnw spring-boot:run -Dmaven.test.skip=true
```


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

5. Run tests

```bash
./mvnw clean install test
```

## Future Plans

- Add alerting (e.g., email on check failures) and Grafana dashboards.
- Implement WebUI for viewing targets and results.
