package com.example.user.profile.service.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class BaseIntegrationTest {

    @Container
    protected static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:14")
                    .withDatabaseName("testdb")
                    .withUsername("postgres")
                    .withPassword("password");

    @Container
    protected static final GenericContainer<?> REDIS =
            new GenericContainer<>("redis:7.2-alpine")
                    .withExposedPorts(6379);

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add(
                "spring.redis.url",
                () -> "redis://" + REDIS.getHost() + ":" + REDIS.getFirstMappedPort()
        );
    }
}
