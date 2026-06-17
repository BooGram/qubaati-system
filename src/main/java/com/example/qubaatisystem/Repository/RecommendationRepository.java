package com.example.qubaatisystem.Repository;

import com.example.qubaatisystem.Model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Integer> {

    Recommendation findRecommendationById(Integer id);
}
