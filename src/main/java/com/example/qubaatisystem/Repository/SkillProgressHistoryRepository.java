package com.example.qubaatisystem.Repository;

import com.example.qubaatisystem.Model.SkillProgressHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillProgressHistoryRepository extends JpaRepository<SkillProgressHistory, Integer> {

    SkillProgressHistory findSkillProgressHistoryById(Integer id);
}
