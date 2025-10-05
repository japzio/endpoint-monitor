package com.japzio.monitor.entity;

import com.japzio.monitor.model.SupportedMethods;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Builder
@Data
@Entity
@Table(name = "targets")
@NoArgsConstructor
@AllArgsConstructor
public class Target {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;
    private String endpoint;
    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private SupportedMethods method;
    private Boolean enabled;
    private Timestamp createdAt;
}