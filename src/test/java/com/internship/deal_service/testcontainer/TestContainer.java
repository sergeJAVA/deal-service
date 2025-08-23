package com.internship.deal_service.testcontainer;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class TestContainer {

    @Container
    public static final GenericContainer<?> redis =
            new GenericContainer<>("redis:8.2.1")
                    .withExposedPorts(6379)
                    .withCommand("redis-server --requirepass testpass");

    @Container
    public static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.4")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("pass");


    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driverClassName", () -> "org.postgresql.Driver");

        registry.add("redis.host", () -> redis.getHost());
        registry.add("redis.port", () -> redis.getMappedPort(6379));
        registry.add("redis.password", () -> "testpass");
    }

}
