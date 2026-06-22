package com.example.qubaatisystem.Repository;

import com.example.qubaatisystem.Model.MissionChoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionChoiceRepository extends JpaRepository<MissionChoice, Integer> {

    MissionChoice findMissionChoiceById(Integer id);

    List<MissionChoice> findMissionChoicesByMissionStepIdOrderByChoiceKeyAsc(Integer missionStepId);

    List<MissionChoice> findMissionChoicesByMissionId(Integer missionId);
}
