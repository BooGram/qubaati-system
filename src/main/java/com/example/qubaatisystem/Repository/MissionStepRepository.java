package com.example.qubaatisystem.Repository;

import com.example.qubaatisystem.Model.MissionStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionStepRepository extends JpaRepository<MissionStep, Integer> {

    MissionStep findMissionStepById(Integer id);

    List<MissionStep> findMissionStepsByMissionIdOrderByStepOrderAsc(Integer missionId);

    MissionStep findMissionStepByMissionIdAndStepOrder(Integer missionId, Integer stepOrder);
}
