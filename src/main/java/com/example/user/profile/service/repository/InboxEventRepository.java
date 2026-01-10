package com.example.user.profile.service.repository;

import com.example.user.profile.service.model.InboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface InboxEventRepository extends JpaRepository<InboxEvent, UUID> {

    @Modifying
    @Query(value = """
            INSERT INTO app.inbox_events (id, created_at)
            VALUES (:id, :createdAt)
            ON CONFLICT (id) DO NOTHING
            """, nativeQuery = true)
    int insertIgnore(@Param("id") UUID id, @Param("createdAt") Instant createdAt);

}
