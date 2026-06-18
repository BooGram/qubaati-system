package com.example.qubaatisystem.Repository;

import com.example.qubaatisystem.Model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Integer> {

    Skill findSkillById(Integer id);

    Skill findSkillByNameIgnoreCase(String name);
}
