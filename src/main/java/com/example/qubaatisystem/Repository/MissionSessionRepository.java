package com.example.qubaatisystem.Repository;

import com.example.qubaatisystem.Model.MissionSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MissionSessionRepository extends JpaRepository<MissionSession, Integer> {

    MissionSession findMissionSessionById(Integer id);
}
