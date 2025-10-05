package com.japzio.monitor.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Builder
@Data
@Entity
@Table(name = "check_results")
@NoArgsConstructor
@AllArgsConstructor
public class CheckResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;
    private UUID targetId;
    private String status;
    private Timestamp createdAt;
}
