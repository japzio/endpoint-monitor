package com.japzio.monitor.repository;

import com.japzio.monitor.entity.CheckResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CheckResultRepository extends JpaRepository<CheckResult, Long> {

    Page<CheckResult> findAllByTargetId(UUID targetId, Pageable pageable);

}