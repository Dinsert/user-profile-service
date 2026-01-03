package com.example.user.profile.service.repository;

import com.example.user.profile.service.model.InboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InboxEventRepository extends JpaRepository<InboxEvent, UUID> {
}
