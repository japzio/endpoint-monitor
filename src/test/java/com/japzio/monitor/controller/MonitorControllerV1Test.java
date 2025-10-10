package com.japzio.monitor.controller;

import com.japzio.monitor.model.external.AddTargetResponse;
import com.japzio.monitor.repository.TargetRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.InfluxDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public class MonitorControllerV1Test {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18")
            .withDatabaseName("endpointmonitor")
            .withUsername("testuser")
            .withPassword("testpass");

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
            .waitingFor(new HttpWaitStrategy());;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("influx.url", influxDBContainer::getUrl);
        registry.add("influx.token", () -> influxDBContainer.getAdminToken().orElse("testpassword")); // Use the helper from Step 3
        registry.add("influx.org", () -> "myorg");
        registry.add("influx.bucket", () -> "endpointmonitoring");
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TargetRepository targetRepository;

    @Test
    void testAddTargetPing_Ok() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Example custom header

        HttpEntity<String> httpEntity = new HttpEntity<>(
                """
                {
                    "endpoint": "ec2.amazonaws.com",
                    "method": "PING"
                }
                """, headers);

        // Act: Send POST request
        ResponseEntity<AddTargetResponse> response = restTemplate
                .postForEntity(
                        "/v1/monitor/targets",
                        httpEntity,
                        AddTargetResponse.class
                );

        // Assert: Verify response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getId()).isNotNull();

        // Verify: Check database
        assertThat(
                targetRepository
                        .findById(UUID.fromString(response.getBody().getId()))
                        .isPresent()
        );
    }

    @Test
    void testAddTargetCurl_Ok() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Example custom header

        HttpEntity<String> httpEntity = new HttpEntity<>(
                """
                {
                    "endpoint": "http://localhost:8080/actuator/info",
                    "method": "CURL"
                }
                """, headers);

        // Act: Send POST request
        ResponseEntity<AddTargetResponse> response = restTemplate
                .postForEntity(
                        "/v1/monitor/targets",
                        httpEntity,
                        AddTargetResponse.class
                );

        // Assert: Verify response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getId()).isNotNull();

        // Verify: Check database
        assertThat(
                targetRepository
                        .findById(UUID.fromString(response.getBody().getId()))
                        .isPresent()
        );
    }

    @Test
    void testAddTargetTelnet_Ok() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Example custom header

        HttpEntity<String> httpEntity = new HttpEntity<>(
                """
                {
                    "endpoint": "localhost:8080",
                    "method": "TELNET"
                }
                """, headers);

        // Act: Send POST request
        ResponseEntity<AddTargetResponse> response = restTemplate
                .postForEntity(
                        "/v1/monitor/targets",
                        httpEntity,
                        AddTargetResponse.class
                );

        // Assert: Verify response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getId()).isNotNull();

        // Verify: Check database
        assertThat(
                targetRepository
                        .findById(UUID.fromString(response.getBody().getId()))
                        .isPresent()
        );
    }

}
