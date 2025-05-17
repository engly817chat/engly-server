package com.engly.engly_server.repository;

import com.engly.engly_server.models.entity.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatisticRepository extends JpaRepository<Statistics, String> {
}
