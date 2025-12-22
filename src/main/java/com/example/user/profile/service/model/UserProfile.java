package com.example.user.profile.service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "user_profiles", schema = "app")
public class UserProfile {

    @Id
    private UUID userId;

    private String loyaltyLevel;

    private BigDecimal externalBalance;
}
