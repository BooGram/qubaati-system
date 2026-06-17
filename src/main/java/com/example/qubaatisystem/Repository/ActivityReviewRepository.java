package com.example.qubaatisystem.Repository;

import com.example.qubaatisystem.Model.ActivityReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityReviewRepository extends JpaRepository<ActivityReview, Integer> {

    ActivityReview findActivityReviewById(Integer id);
}
