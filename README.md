# Endpoint Monitor

`endpoint-monitor` - Simple sefl-hosted endpoint monitoring supports curl(java-curl), ping(InetAddress.isReachable(..)) and telnet(commons-net TelnetClient)

Note: This is purely rest api interaction. WebUI is still to follow :-D

# Tech Stack

- Java 21
- Spring Boot 3.5.X
- PostgreSQL 18
- Liquibase
- TestContainers(requires Docker)
- Task dependencies 
    - [java-curl](https://github.com/rockswang/java-curl)
    - [apache commons-net](https://commons.apache.org/proper/commons-net/index.html)
    - [InetAddress](https://docs.oracle.com/en/java/javase/21/docs/api//java.base/java/net/class-use/InetAddress.html)

# Local Dev Environment

- Setup Docker
- PostgreSQL local or can be docker
- Import [changelog-000-bootstrap.sql](src/main/resources/db/changelog/changelog-000-bootstrap.sql)
  - Succeeding database changes are here [classpath:db/changelog/](src/main/resources/db/changelog)
- Update [application-local.yml](src/main/resources/application-local.yml) especially for datasource and liquibase related configs
- Try to run the application, liquibase should be enabled -local.yml
