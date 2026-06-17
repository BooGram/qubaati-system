package com.example.qubaatisystem.Repository;

import com.example.qubaatisystem.Model.MissionSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionSessionRepository extends JpaRepository<MissionSession, Integer> {

    List<MissionSession> findMissionSessionById(Integer id);
}
