package com.japzio.monitor.repository;

import com.japzio.monitor.entity.Target;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TargetRepository extends JpaRepository<Target, Long> {

    List<Target> findAllByEnabledTrue();

}