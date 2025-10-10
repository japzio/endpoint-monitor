package com.japzio.monitor.model.entity;

import com.japzio.monitor.model.SupportedMethods;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
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

    @Column(nullable = false, length = 255)
    private String endpoint;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private SupportedMethods method;

    @Column(insertable = false)
    private Boolean enabled;

    @Column(insertable = false)
    private Timestamp createdAt;

    private Long timeout;
}