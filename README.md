# Endpoint Monitor

`endpoint-monitor` - Simple sefl-hosted endpoint monitoring supports curl(java-curl), ping(InetAddress.isReachable(..)) and telnet(commons-net TelnetClient)

Note: This is purely rest api interaction. WebUI is still to follow :-D

# Tech Stack

- Java 21
- Spring Boot
- PostgreSQL 18
- Liquibase
- TestContainers(requires Docker)

# Local Dev Environment

- Setup Docker
- PostgreSQL local or can be docker
- Import [changelog-000-bootstrap.sql](src/main/resources/db/changelog/changelog-000-bootstrap.sql)
- Update [application-local.yml](src/main/resources/application-local.yml) especially for datasource and liquibase related configs
- Try to run the application, liquibase should be enabled -local.yml