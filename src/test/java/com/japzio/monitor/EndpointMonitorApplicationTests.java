package com.japzio.monitor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.InfluxDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitStrategyTarget;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class EndpointMonitorApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18")
            .withDatabaseName("endpointmonitor")
            .withUsername("testuser")
            .withPassword("testpass")
            .waitingFor(new HostPortWaitStrategy());

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.liquibase.url", postgres::getJdbcUrl);
        registry.add("spring.liquibase.user", postgres::getUsername);
        registry.add("spring.liquibase.password", postgres::getPassword);
    }

    @Container
    static InfluxDBContainer<?> influxDBContainer = new InfluxDBContainer<>(
            DockerImageName.parse("influxdb:2.7"))
            .withEnv("DOCKER_INFLUXDB_INIT_MODE", "setup")
            .withEnv("DOCKER_INFLUXDB_INIT_USERNAME", "admin")
            .withEnv("DOCKER_INFLUXDB_INIT_PASSWORD", "testpassword")
            .withEnv("DOCKER_INFLUXDB_INIT_ORG", "testorg")
            .withEnv("DOCKER_INFLUXDB_INIT_BUCKET", "testbucket")
            .waitingFor(new HttpWaitStrategy());

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("influx.url", influxDBContainer::getUrl);
        registry.add("influx.token", () -> influxDBContainer.getAdminToken().orElse("testpassword")); // Use the helper from Step 3
        registry.add("influx.org", () -> "myorg");
        registry.add("influx.bucket", () -> "endpointmonitoring");
    }

    @Test
    void ensureActuators_Ok() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Request-Reference-No", UUID.randomUUID().toString());

        // Act: Send GET request
        ResponseEntity<String> response = restTemplate
                .getForEntity(
                        "/actuator/info",
                        String.class,
                        headers
                );

        // Assert: Verify response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

}
