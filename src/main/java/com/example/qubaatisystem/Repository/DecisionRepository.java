package com.example.qubaatisystem.Repository;

import com.example.qubaatisystem.Model.Decision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DecisionRepository extends JpaRepository<Decision, Integer> {

    Decision findDecisionById(Integer id);
}
