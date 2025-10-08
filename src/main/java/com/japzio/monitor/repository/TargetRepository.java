package com.japzio.monitor.repository;

import com.japzio.monitor.entity.Target;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TargetRepository extends JpaRepository<Target, Long> {

    List<Target> findAllByEnabledTrue();
    Optional<Target> findById(UUID id);
    void deleteById(UUID id);
    boolean existsById(UUID id);
}