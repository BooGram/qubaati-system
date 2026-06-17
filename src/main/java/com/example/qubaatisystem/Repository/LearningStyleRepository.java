package com.example.qubaatisystem.Repository;

import com.example.qubaatisystem.Model.LearningStyle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningStyleRepository extends JpaRepository<LearningStyle, Integer> {

    List<LearningStyle> findLearningStyleById(Integer id);
}
