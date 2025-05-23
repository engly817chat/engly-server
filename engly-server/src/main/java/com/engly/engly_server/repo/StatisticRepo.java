package com.engly.engly_server.repo;

import com.engly.engly_server.models.entity.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatisticRepo extends JpaRepository<Statistics, String> {
}
