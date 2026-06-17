package com.example.qubaatisystem.Repository;

import com.example.qubaatisystem.Model.Insight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsightRepository extends JpaRepository<Insight, Integer> {

    Insight findInsightById(Integer id);
}
