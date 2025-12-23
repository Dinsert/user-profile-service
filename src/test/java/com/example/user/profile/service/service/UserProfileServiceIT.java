package com.example.user.profile.service.service;

import com.example.user.profile.service.UserProfileServiceApplication;
import com.example.user.profile.service.config.TestCacheConfig;
import com.example.user.profile.service.repository.UserProfileRepository;
import com.example.user.profile.service.util.UserProfileFixture;
import com.example.userprofile.api.dto.UserProfileDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(classes = {UserProfileServiceApplication.class, TestCacheConfig.class})
class UserProfileServiceIT {

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("password");

    @Container
    public static GenericContainer<?> redis = new GenericContainer<>("redis:7.2-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        String redisUrl = "redis://" + redis.getHost() + ":" + redis.getFirstMappedPort();
        registry.add("spring.redis.url", () -> redisUrl);
    }

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private CacheManager cacheManager;

    private static final String CACHE = "user_profiles";

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @AfterEach
    void tearDown() {
        userProfileRepository.deleteAll();
        cacheManager.getCache(CACHE).clear();
    }

    @Test
    void createAndGetUserProfile_shouldCacheResult() {
        UserProfileDto dto = UserProfileFixture.createDtoForCreateEntity();

        userProfileService.createUserProfile(userId, dto);

        UserProfileDto response = userProfileService.getUserProfile(userId);

        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(dto);
        assertThat(cacheManager.getCache(CACHE).get(userId)).isNotNull();
        assertThat(cacheManager.getCache(CACHE).get(userId, UserProfileDto.class)).isEqualTo(response);
    }

    @Test
    void updateUserProfile_shouldEvictCache() {
        UserProfileDto dto = UserProfileFixture.createDtoForCreateEntity();
        userProfileService.createUserProfile(userId, dto);
        userProfileService.getUserProfile(userId);
        UserProfileDto dtoForUpdate = UserProfileFixture.createDtoForUpdateEntity();

        userProfileService.updateUserProfile(userId, dtoForUpdate);

        assertThat(cacheManager.getCache(CACHE).get(userId)).isNull();
    }
}